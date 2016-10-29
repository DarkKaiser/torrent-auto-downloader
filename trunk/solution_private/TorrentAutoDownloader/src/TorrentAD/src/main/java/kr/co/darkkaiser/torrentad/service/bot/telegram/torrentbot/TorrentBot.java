package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.DefaultRequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.HelpRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.TorrentStatusRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardListRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardSelectRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardSelectedRequestHandler;
import kr.co.darkkaiser.torrentad.util.OutParam;
import kr.co.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;

public class TorrentBot extends TelegramLongPollingBot implements TorrentBotResource {

	private static final Logger logger = LoggerFactory.getLogger(TorrentBot.class);

	private final ConcurrentHashMap<Long/* CHAT_ID */, ChatRoom> chatRooms = new ConcurrentHashMap<>();

	private final WebSiteConnector siteConnector;

	private final RequestHandlerRegistry requestHandlerRegistry = new DefaultRequestHandlerRegistry();

	public TorrentBot(ImmediatelyTaskExecutorService immediatelyTaskExecutorService, Configuration configuration) throws Exception {
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.siteConnector = new DefaultWebSiteConnector(TorrentBot.class.getSimpleName(), configuration);
		
		// 사용가능한 RequestHandler를 모두 등록한다.
		this.requestHandlerRegistry.register(new WebSiteBoardSelectRequestHandler(this));
		this.requestHandlerRegistry.register(new WebSiteBoardSelectedRequestHandler(this, this.requestHandlerRegistry));
		this.requestHandlerRegistry.register(new WebSiteBoardListRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new TorrentStatusRequestHandler());
		this.requestHandlerRegistry.register(new HelpRequestHandler(this.requestHandlerRegistry));
	}

	@Override
	public void dispose() {
		if (this.siteConnector != null)
			this.siteConnector.logout();

		this.chatRooms.clear();
	}

	@Override
	public WebSite getSite() {
		return this.siteConnector.getSite();
	}
	
	@Override
	public WebSiteConnector getSiteConnector() {
		return this.siteConnector;
	}

	@Override
	public TorrentClient getTorrentClient() {
		// @@@@@
		return null;
	}

	@Override
	public String getBotUsername() {
		return "darkkaiser_torrentad_bot";
	}

	@Override
	public String getBotToken() {
		return "298010919:AAEcnGlwklr6PXMQ4UUB7JbgMN3zTqrr6r4";
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update == null)
			throw new NullPointerException("update");

		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();

	            // 수신된 메시지를 명령+파라메터로 분리한다.
	            OutParam<String> outCommand = new OutParam<>();
				OutParam<String[]> outParameters = new OutParam<>();
				OutParam<Boolean> outContainInitialChar = new OutParam<>();
	            BotCommandUtils.parse(message.getText(), outCommand, outParameters, outContainInitialChar);

	            // 해당 요청을 처리할 수 있는 RequestHandler를 찾는다.
				RequestHandler requestHandler = this.requestHandlerRegistry.getRequestHandler(outCommand.get(), outParameters.get(), outContainInitialChar.get());
				if (requestHandler != null) {
					ChatRoom chatRoom = getChatRoom(message.getChat().getId());
					requestHandler.execute(this, chatRoom, outCommand.get(), outParameters.get(), outContainInitialChar.get());

					return;
				}
			}
			
			// @@@@@
			//////////////////////////////////////////////////////////
			CallbackQuery callbackQuery = update.getCallbackQuery();
			if (callbackQuery != null) {
				// 인라인키보드 반환
//				EditMessageReplyMarkup e;
				System.out.println(callbackQuery);
				return;
			}
			//////////////////////////////////////////////////////////
			
			onUnknownCommandMessage(update);
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	private void onUnknownCommandMessage(Update update) {
		StringBuilder sbAnswerMessage = new StringBuilder();

		Message message = update.getMessage();
		if (message != null && message.hasText() == true)
			sbAnswerMessage.append("'").append(message.getText()).append("'는 등록되지 않은 명령어입니다.\n");

		BotCommand botCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(HelpRequestHandler.class);
		sbAnswerMessage.append("명령어를 모르시면 '").append(botCommand.getCommand()).append("'을 입력하세요.");

		SendMessage answerMessage = new SendMessage()
				.setChatId(message.getChatId().toString())
				.setText(sbAnswerMessage.toString())
				.enableHtml(true);

		try {
			sendMessage(answerMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

	private ChatRoom getChatRoom(Long chatId) {
		ChatRoom chatRoom = this.chatRooms.get(chatId);
		if (chatRoom == null) {
			chatRoom = new ChatRoom(chatId);
			this.chatRooms.put(chatId, chatRoom);
		}

		return chatRoom;
	}

}


// @@@@@
//@Override
//public boolean execute(AbsSender absSender, Update update) {
//	try {
//		if (update.hasMessage() == true) {
//            Message message = update.getMessage();
//
//            String commandMessage = message.getText();
//			String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);
//
//			String command = commandSplit[0];
//			if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
//				command = command.substring(1);
//
//			if (this.handlerMap.containsKey(command) == true) {
//				String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
//				this.handlerMap.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
//				return true;
//			}
//
//            // 검색어나 기타 다른것인지 확인
////            Long chatId = message.getChatId();
//            // @@@@@
//        }
//
////		onCommandUnknownMessage(update);
//	} catch (Exception e) {
//		logger.error(null, e);
//	}
//
//	return false;
//}
//// @@@@@
//public final boolean executeCommand(AbsSender absSender, Message message) {
//	if (absSender == null)
//		throw new NullPointerException("absSender");
//	if (message == null)
//		throw new NullPointerException("message");
//
//	if (message.hasText() == true) {
//		String commandMessage = message.getText();
//		String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);
//
//		String command = commandSplit[0];
//		if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
//			command = command.substring(1);
//
//		if (commands.containsKey(command) == true) {
//			String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
//			commands.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
//			return true;
//		}
//	}
//
//	return false;
//}

// @@@@@
// 인라인키보드는 콜백쿼리가 들어옴
//System.out.println(update.getCallbackQuery() + " : " + update);//@@@@@
//CallbackQuery callbackQuery = update.getCallbackQuery();

// chat_id를 구해서 해당 user를 구한다.
// user에서 이전에 실행된 Request를 구한다.
// 신규 Request에 이전 메시지를 넘겨주면서 execute???
	// 반환값으로 NoneRequest 혹은 신규 Request를 받아서 다시 저장해둔다.


//if (신규 객체가 SearchingRequest 객체라면) {
//	if (이전 객체가 InputSearchKeywordResponse 아니라면)
//		이전객체.cancel();
//
//	user.execute(신규객체Request, 이전객체Response);
//}
//
//try {
//	Request request = this.requestResponseRegistry.getRequest(update);
//	if (request != null) {
////		if (update.hasMessage() == true) {
////        Message message = update.getMessage();
////        if (this.commandRegistry.executeCommand(this, message))
////            return;
////
////        // 검색어나 기타 다른것인지 확인
////        Long chatId = message.getChatId();
////        // @@@@@
////    }
//        Message message = update.getMessage();
//
//        String commandMessage = message.getText();
//		String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);
//
//		String command = commandSplit[0];
//		if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
//			command = command.substring(1);
//
//		String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
//
//        Long chatId = message.getChatId();
//        ChatRoom user = this.chats.get(chatId);
//        if (user != null) {
//        	Response response = user.getResponse();
//        	if (response != null) {
//        		if (response.allow(request) == false) {
//        			response.cancel(this, message.getFrom(), message.getChat());
//        		}
//        	}
//        }
//        
////        request.prepareExecute(response);
//		
//		Response execute = request.execute(this, message.getFrom(), message.getChat(), parameters);
//		user.setResponse(execute);
//	} else {
//		onUnknownCommandMessage(update);
//	}
//} catch (Exception e) {
//	logger.error(null, e);
//}