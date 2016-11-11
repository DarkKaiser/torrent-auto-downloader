package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.DefaultRequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.HelpRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.TorrentStatusRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardItemDownloadLinkListRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardItemDownloadRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardListRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardListResultCallbackQueryRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardSelectRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardSelectedRequestHandler;
import kr.co.darkkaiser.torrentad.util.OutParam;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import kr.co.darkkaiser.torrentad.util.metadata.repository.MetadataRepositoryImpl;
import kr.co.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;

public class TorrentBot extends TelegramLongPollingBot implements TorrentBotResource {

	private static final Logger logger = LoggerFactory.getLogger(TorrentBot.class);

	private final ConcurrentHashMap<Long/* CHAT_ID */, ChatRoom> chatRooms = new ConcurrentHashMap<>();

	private final WebSiteConnector siteConnector;
	
	// @@@@@
	private TorrentClient torrentClient;
	
	// @@@@@
	private AES256Util aes256;
	
	private final RequestHandlerRegistry requestHandlerRegistry = new DefaultRequestHandlerRegistry();
	
	private final Configuration configuration;
	
	private final MetadataRepository metadataRepository;

	public TorrentBot(ImmediatelyTaskExecutorService immediatelyTaskExecutorService, Configuration configuration) throws Exception {
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.metadataRepository = new MetadataRepositoryImpl(Constants.METADATA_REPOSITORY_FILE_NAME);
		
		this.siteConnector = new DefaultWebSiteConnector(TorrentBot.class.getSimpleName(), configuration);
		
		// 사용가능한 RequestHandler를 모두 등록한다.
		this.requestHandlerRegistry.register(new WebSiteBoardSelectRequestHandler(this));
		this.requestHandlerRegistry.register(new WebSiteBoardSelectedRequestHandler(this, this.requestHandlerRegistry));
		this.requestHandlerRegistry.register(new WebSiteBoardListRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new TorrentStatusRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new HelpRequestHandler(this.requestHandlerRegistry));
		this.requestHandlerRegistry.register(new WebSiteBoardListResultCallbackQueryRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardItemDownloadLinkListRequestHandler(this, immediatelyTaskExecutorService));
		this.requestHandlerRegistry.register(new WebSiteBoardItemDownloadRequestHandler(this, immediatelyTaskExecutorService));
		
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
	public WebSiteConnector getSiteConnector() {
		return this.siteConnector;
	}

	@Override
	public TorrentClient getTorrentClient() {
		// @@@@@
		if (this.torrentClient == null) {
			String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
			String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
			String password = "";
			try {
				password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			this.torrentClient = new TransmissionRpcClient(url);
			try {
				if (this.torrentClient.connect(id, password) == false) {
//				logger.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return this.torrentClient;
	}

	private void initChatRooms() {
		String chatIds = this.metadataRepository.getString(Constants.MR_ITEM_BOT_SERVICE_REGISTERED_CHAT_IDS, "");
		String[] chatIdArrays = chatIds.split(Constants.MR_ITEM_BOT_SERVICE_REGISTERED_CHAT_IDS_SEPARATOR);
		for (String chatId : chatIdArrays) {
			if (StringUtil.isBlank(chatId) == false) {
				try {
					newChatRoom(Long.parseLong(chatId));
				} catch (NumberFormatException e) {
				}
			}
		}
	}
	
	// @@@@@
	protected String decode(String encryption) throws Exception {
		if (this.aes256 == null)
			this.aes256 = new AES256Util();

		try {
			return this.aes256.decode(encryption);
		} catch (Exception e) {
//			logger.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", encryption);
			throw e;
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
	            BotCommandUtils.parseBotCommand(message.getText(), outCommand, outParameters, outContainInitialChar);

	            // 해당 요청을 처리할 수 있는 RequestHandler를 찾는다.
				RequestHandler requestHandler = this.requestHandlerRegistry.getRequestHandler(outCommand.get(), outParameters.get(), outContainInitialChar.get());
				if (requestHandler != null) {
					ChatRoom chatRoom = getChatRoom(message.getChat().getId());
					requestHandler.execute(this, chatRoom, update, outCommand.get(), outParameters.get(), outContainInitialChar.get());
					return;
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
					requestHandler.execute(this, chatRoom, update, outCommand.get(), outParameters.get(), outContainInitialChar.get());
					return;
				}
			}
			
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

		BotCommandUtils.sendMessage(this, message.getChatId(), sbAnswerMessage.toString());
	}

	private ChatRoom getChatRoom(Long chatId) {
		ChatRoom chatRoom = this.chatRooms.get(chatId);
		if (chatRoom == null) {
			chatRoom = newChatRoom(chatId);

			// 새로 생성된 대화방의 ID를저장한다.
			StringBuilder sbValue = new StringBuilder();
			for (Long id : this.chatRooms.keySet()) {
				sbValue.append(id).append(Constants.MR_ITEM_BOT_SERVICE_REGISTERED_CHAT_IDS_SEPARATOR);
			}
			
			this.metadataRepository.setString(Constants.MR_ITEM_BOT_SERVICE_REGISTERED_CHAT_IDS, sbValue.toString());
		}

		return chatRoom;
	}

	private ChatRoom newChatRoom(Long chatId) {
		assert this.chatRooms.get(chatId) == null;
		ChatRoom chatRoom = new ChatRoom(chatId, this.siteConnector.getSite(), this.metadataRepository);
		this.chatRooms.put(chatId, chatRoom);
		return chatRoom;
	}

}
