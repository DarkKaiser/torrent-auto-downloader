package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
		return TorrentBotConfig.BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return TorrentBotConfig.BOT_TOKEN;
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
				
				AnswerCallbackQuery a = new AnswerCallbackQuery();
				a.setCallbackQueryId(callbackQuery.getId());
//				a.setText("ddd");
				answerCallbackQuery(a);
				
				
				
				Message message = callbackQuery.getMessage();
				
				EditMessageText e = new EditMessageText();
				e.setChatId(message.getChat().getId().toString())
				.setMessageId(message.getMessageId())
				.setText("ddd");
				
			      InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
			      
			      List<InlineKeyboardButton> keyboard = new ArrayList<>();
			      InlineKeyboardButton keyboardFirstRow = new InlineKeyboardButton();
			      keyboardFirstRow.setText("새 텍스트");
			      keyboardFirstRow.setCallbackData("callbackData");
			      keyboard.add(keyboardFirstRow);

			      InlineKeyboardButton keyboardFirstRow2 = new InlineKeyboardButton();
			      keyboardFirstRow2.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
			      keyboardFirstRow2.setCallbackData("keyboardFirstRow2");
			      keyboard.add(keyboardFirstRow2);
			      
			      List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
			      InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
			      keyboardFirstRow3.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
			      keyboardFirstRow3.setCallbackData("keyboardFirstRow3");
			      keyboard2.add(keyboardFirstRow3);
			      
			      List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
			      keyboards.add(keyboard);
			      keyboards.add(keyboard2);

					inline.setKeyboard(keyboards);

				e.setReplyMarkup(inline);
				
				editMessageText(e);

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
