package com.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.net.torrent.TorrentClient;
import com.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.DefaultRequestHandlerRegistry;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.*;
import com.darkkaiser.torrentad.util.OutParam;
import com.darkkaiser.torrentad.util.crypto.AES256Util;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepositoryImpl;
import com.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteConnector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TorrentBot extends TelegramLongPollingBot implements TorrentBotResource {

	private final ConcurrentHashMap<Long/* CHAT_ID */, ChatRoom> chatRooms = new ConcurrentHashMap<>();

	@Getter
	private final WebSiteConnector siteConnector;

	private TorrentClient torrentClient;
	
	private final RequestHandlerRegistry requestHandlerRegistry = new DefaultRequestHandlerRegistry();
	
	private final Configuration configuration;
	
	private final MetadataRepository metadataRepository;

	public TorrentBot(final ImmediatelyTaskExecutorService immediatelyTaskExecutorService, final FileTransmissionExecutorService fileTransmissionExecutorService, final Configuration configuration) throws Exception {
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");
		Objects.requireNonNull(fileTransmissionExecutorService, "fileTransmissionExecutorService");
		Objects.requireNonNull(configuration, "configuration");

		this.configuration = configuration;
		this.metadataRepository = new MetadataRepositoryImpl(Constants.BOT_SERVICE_METADATA_REPOSITORY_FILE_NAME);
		
		this.siteConnector = new DefaultWebSiteConnector(TorrentBot.class.getSimpleName(), configuration);

		// 사용가능한 RequestHandler를 모두 등록한다.
		this.requestHandlerRegistry.register(new WebSiteBoardSelectRequestHandler(this));
		this.requestHandlerRegistry.register(new WebSiteBoardSelectedRequestHandler(this, this.requestHandlerRegistry));
		this.requestHandlerRegistry.register(new WebSiteBoardListRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardSearchRequestHandler(this, immediatelyTaskExecutorService, this.requestHandlerRegistry));
		this.requestHandlerRegistry.register(new WebSiteBoardSearchInlineKeyboardRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new TorrentStatusRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new TorrentStatusResultCallbackQueryRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new HelpRequestHandler(this.requestHandlerRegistry));
		this.requestHandlerRegistry.register(new WebSiteBoardListResultCallbackQueryRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardSearchResultCallbackQueryRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardListResultDownloadLinkInquiryRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardSearchResultDownloadLinkInquiryRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardItemDownloadRequestHandler(this, immediatelyTaskExecutorService, fileTransmissionExecutorService));

		initChatRooms();
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
	public TorrentClient getTorrentClient() {
		if (this.torrentClient != null && this.torrentClient.isConnected() == true) 
			return this.torrentClient;

		String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
		String password = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD);

		try {
			password = new AES256Util().decode(password);
		} catch (final Exception e1) {
			log.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", password);
			return null;
		}

		this.torrentClient = new TransmissionRpcClient(url);
		
		try {
			if (this.torrentClient.connect(id, password) == false)
				log.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
		} catch (final Exception e2) {
			log.error("토렌트 서버 접속이 실패하였습니다.", e2);
			return null;
		}

		return this.torrentClient;
	}

	private void initChatRooms() {
		String chatIds = this.metadataRepository.getString(Constants.BOT_SERVICE_MR_KEY_REGISTERED_CHAT_IDS, "");
		String[] chatIdArrays = chatIds.split(Constants.BOT_SERVICE_MR_KEY_REGISTERED_CHAT_IDS_SEPARATOR);
		for (final String chatId : chatIdArrays) {
			if (StringUtil.isBlank(chatId) == false) {
				try {
					newChatRoom(Long.parseLong(chatId));
				} catch (final NumberFormatException ignored) {
				}
			}
		}
	}
	
	@Override
	public String getBotUsername() {
		return this.configuration.getValue(Constants.APP_CONFIG_TAG_TELEGRAM_TORRENTBOT_USERNAME);
	}

	@Override
	public String getBotToken() {
		return this.configuration.getValue(Constants.APP_CONFIG_TAG_TELEGRAM_TORRENTBOT_TOKEN);
	}

	@Override
	public void onUpdateReceived(final Update update) {
		Objects.requireNonNull(update, "update");

		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();

	            // 수신된 메시지를 명령+파라메터로 분리한다.
	            OutParam<String> outCommand = new OutParam<>();
				OutParam<String[]> outParameters = new OutParam<>();
				OutParam<Boolean> outContainInitialChar = new OutParam<>();
	            BotCommandUtils.parseBotCommand(message.getText(), outCommand, outParameters, outContainInitialChar);

				// 해당 요청을 처리할 수 있는 RequestHandler를 찾는다.
	            ChatRoom chatRoom = getChatRoom(message.getChat().getId());
				RequestHandler requestHandler = this.requestHandlerRegistry.getRequestHandler(outCommand.get(), outParameters.get(), outContainInitialChar.get());
				if (requestHandler != null) {
					chatRoom.setLatestRequestHandler(requestHandler);
					requestHandler.execute(this, chatRoom, update, outCommand.get(), outParameters.get(), outContainInitialChar.get());
					return;
				}

				// 처리할 수 있는 RequestHandler를 찾지 못한 경우, 이전에 입력된 요청이 검색 요청인지 확인하여 처리한다.
				RequestHandler latestRequestHandler = chatRoom.getLatestRequestHandler();
				if (latestRequestHandler != null && WebSiteBoardSearchInlineKeyboardRequestHandler.class.isInstance(latestRequestHandler) == true) {
					requestHandler = this.requestHandlerRegistry.getRequestHandler(WebSiteBoardSearchRequestHandler.class);
					if (requestHandler != null) {
						chatRoom.setLatestRequestHandler(requestHandler);

						// 검색어가 command와 parameters로 분리되어 수신되므로, 이를 합하여 parameters로 만든다.
						List<String> parameters = new ArrayList<String>() { {
							add(outCommand.get());
						} };

						parameters.addAll(Arrays.asList(outParameters.get()));

						requestHandler.execute(this, chatRoom, update, ((BotCommand) requestHandler).getCommand(), parameters.toArray(new String[parameters.size()]), outContainInitialChar.get());
						
						return;
					}
				}
			}

			CallbackQuery callbackQuery = update.getCallbackQuery();
			if (callbackQuery != null) {
				String data = callbackQuery.getData();
				Message message = callbackQuery.getMessage();

	            // 수신된 메시지를 명령+파라메터로 분리한다.
	            OutParam<String> outCommand = new OutParam<>();
				OutParam<String[]> outParameters = new OutParam<>();
				OutParam<Boolean> outContainInitialChar = new OutParam<>();
	            BotCommandUtils.parseBotCommand(data, outCommand, outParameters, outContainInitialChar);

	            // 해당 요청을 처리할 수 있는 RequestHandler를 찾는다.
				RequestHandler requestHandler = this.requestHandlerRegistry.getRequestHandler(outCommand.get(), outParameters.get(), outContainInitialChar.get());
				if (requestHandler != null) {
					ChatRoom chatRoom = getChatRoom(message.getChat().getId());
					chatRoom.setLatestRequestHandler(requestHandler);
					requestHandler.execute(this, chatRoom, update, outCommand.get(), outParameters.get(), outContainInitialChar.get());
					return;
				}
			}
			
			onUnknownCommandMessage(update);
		} catch (final Exception e) {
			log.error(null, e);
		}
	}

	private void onUnknownCommandMessage(final Update update) {
		StringBuilder sbAnswerMessage = new StringBuilder();

		Message message = update.getMessage();
		if (message != null && message.hasText() == true)
			sbAnswerMessage.append("'").append(message.getText()).append("'는 등록되지 않은 명령어입니다.\n");

		BotCommand botCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(HelpRequestHandler.class);
		sbAnswerMessage.append("명령어를 모르시면 '/").append(botCommand.getCommand()).append("'을 입력하세요.");

		if (message != null) {
			BotCommandUtils.sendMessage(this, message.getChatId(), sbAnswerMessage.toString());
		}
	}

	private ChatRoom getChatRoom(final Long chatId) {
		ChatRoom chatRoom = this.chatRooms.get(chatId);
		if (chatRoom == null) {
			chatRoom = newChatRoom(chatId);

			// 새로 생성된 대화방의 ID를저장한다.
			StringBuilder sbValue = new StringBuilder();
			for (final Long id : this.chatRooms.keySet()) {
				sbValue.append(id).append(Constants.BOT_SERVICE_MR_KEY_REGISTERED_CHAT_IDS_SEPARATOR);
			}
			
			this.metadataRepository.setString(Constants.BOT_SERVICE_MR_KEY_REGISTERED_CHAT_IDS, sbValue.toString());
		}

		return chatRoom;
	}

	private ChatRoom newChatRoom(final Long chatId) {
		assert this.chatRooms.get(chatId) == null;
		ChatRoom chatRoom = new ChatRoom(chatId, this.siteConnector.getSite(), this.metadataRepository);
		this.chatRooms.put(chatId, chatRoom);
		return chatRoom;
	}

}
