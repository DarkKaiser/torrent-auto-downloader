package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.commands.BotCommand;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.DefaultRequestResponseRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestResponseRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.HelpRequest;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.ListRequest;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.SearchingRequest;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;
import kr.co.darkkaiser.torrentad.util.Disposable;

public class TelegramTorrentBot extends TelegramLongPollingBot implements Disposable {

	private static final Logger logger = LoggerFactory.getLogger(TelegramTorrentBot.class);

	// @@@@@
	private final ConcurrentHashMap<Long/* CHAT_ID */, Chat> chats = new ConcurrentHashMap<>();

	// @@@@@
	private final RequestResponseRegistry requestResponseRegistry = new DefaultRequestResponseRegistry();
	
	// @@@@@
	private TorrentJob job;
	
	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;

	private final Configuration configuration;
	
	public TelegramTorrentBot(ImmediatelyTaskExecutorService immediatelyTaskExecutorService, Configuration configuration) throws Exception {
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;

		this.requestResponseRegistry.register(new ListRequest());
		this.requestResponseRegistry.register(new SearchingRequest());
		this.requestResponseRegistry.register(new HelpRequest(this.requestResponseRegistry));

		// @@@@@
		this.job = new TorrentJob(immediatelyTaskExecutorService, this.configuration);
//        int state = userState.getOrDefault(message.getFrom().getId(), 0);
//        userState.put(message.getFrom().getId(), WAITINGCHANNEL);
//        userState.remove(message.getFrom().getId());
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
		// @@@@@
		if (update == null)
			throw new NullPointerException("update");

		// 인라인키보드는 콜백쿼리가 들어옴
		System.out.println(update.getCallbackQuery() + " : " + update);//@@@@@
		
		// chat_id를 구해서 해당 user를 구한다.
		// user에서 이전에 실행된 Request를 구한다.
		// 신규 Request에 이전 메시지를 넘겨주면서 execute???
			// 반환값으로 NoneRequest 혹은 신규 Request를 받아서 다시 저장해둔다.


//		if (신규 객체가 SearchingRequest 객체라면) {
//			if (이전 객체가 InputSearchKeywordResponse 아니라면)
//				이전객체.cancel();
//
//			user.execute(신규객체Request, 이전객체Response);
//		}

		
		try {
			Request request = this.requestResponseRegistry.get(update);
			if (request != null) {
	            Message message = update.getMessage();

	            String commandMessage = message.getText();
				String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);

				String command = commandSplit[0];
				if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
					command = command.substring(1);

				String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
				
				
	            Long chatId = message.getChatId();
	            Chat user = this.chats.get(chatId);
	            if (user != null) {
	            	Response response = user.getResponse();
	            	if (response != null) {
	            		if (response.allow(request) == false) {
	            			response.cancel(this, message.getFrom(), message.getChat());
	            		}
	            	}
	            }
				
				Response execute = request.execute(this, message.getFrom(), message.getChat(), parameters);
				user.setResponse(execute);
				
				return;
			}
			
			//this.job.getTorrentStatus();
			this.job.list();
			this.job.list();
//			this.job.search(0, 0);
			
			onCommandUnknownMessage(update);
			
//			if (update.hasMessage() == true) {
//	            Message message = update.getMessage();
//	            if (this.commandRegistry.executeCommand(this, message))
//	                return;
//
//	            // 검색어나 기타 다른것인지 확인
//	            Long chatId = message.getChatId();
//	            // @@@@@
//	        }
//
//			onCommandUnknownMessage(update);
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	private void onCommandUnknownMessage(Update update) {
		if (update == null)
			throw new NullPointerException("update");

		// @@@@@
		if (update.hasMessage() == true) {
			Message message = update.getMessage();
//			CallbackQuery callbackQuery = update.getCallbackQuery();
//			message = callbackQuery.getMessage();

			StringBuilder sbMessage = new StringBuilder();
			if (message != null && message.hasText() == true)
				sbMessage.append("'").append(message.getText()).append("'는 등록되지 않은 명령어입니다.\n");

			sbMessage.append("명령어를 모르시면 '도움'을 입력하세요.");

			SendMessage commandUnknownMessage = new SendMessage();
			commandUnknownMessage.setChatId(message.getChatId().toString());
//			commandUnknownMessage.setText(callbackQuery.getData());
			commandUnknownMessage.setText(sbMessage.toString());

			try {
				sendMessage(commandUnknownMessage);
			} catch (TelegramApiException e) {
				logger.error(null, e);
			}
		}
	}

	@Override
	public void dispose() {
		// @@@@@
//		this.job.dispose();
	}

}
