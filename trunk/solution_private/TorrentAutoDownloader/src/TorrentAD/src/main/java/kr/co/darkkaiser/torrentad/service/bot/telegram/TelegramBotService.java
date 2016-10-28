package kr.co.darkkaiser.torrentad.service.bot.telegram;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.BotService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBot;

public class TelegramBotService implements BotService {

	private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
	
	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;

	private TelegramBotsApi botsApi;

	private TorrentBot torrentBot;
	
	private BotSession torrentBotSession;

	private final Configuration configuration;

	public TelegramBotService(ImmediatelyTaskExecutorService immediatelyTaskExecutorService, Configuration configuration) {
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}

	@Override
	public boolean start() throws Exception {
		if (this.configuration == null)
			throw new NullPointerException("configuration");

		BotLogger.setLevel(Level.ALL);
		BotLogger.registerLogger(new ConsoleHandler());

		this.botsApi = new TelegramBotsApi();

		try {
			this.torrentBot = new TorrentBot(this.immediatelyTaskExecutorService, this.configuration);
			this.torrentBotSession = this.botsApi.registerBot(this.torrentBot);
		} catch (TelegramApiException e) {
			logger.error(null, e);
			return false;
		}

		return true;
	}

	@Override
	public void stop() {
		// 종료할 때 TelegramBot API에서 예외가 항상 발생하므로 로그를 출력하지 않도록 한다.
		BotLogger.setLevel(Level.OFF);

		if (this.torrentBotSession != null) {
			this.torrentBotSession.close();
			this.torrentBotSession = null;
		}

		if (this.torrentBot != null) {
			this.torrentBot.dispose();
			this.torrentBot = null;
		}

		this.botsApi = null;
	}

}
