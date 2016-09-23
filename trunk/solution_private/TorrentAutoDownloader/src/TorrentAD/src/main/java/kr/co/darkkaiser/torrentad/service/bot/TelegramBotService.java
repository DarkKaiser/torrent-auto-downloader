package kr.co.darkkaiser.torrentad.service.bot;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.service.bot.bots.TorrentAdBot;

public class TelegramBotService implements Service {

	private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
	
	private TelegramBotsApi telegramBotsApi;
	
	private final Configuration configuration;

	public TelegramBotService(Configuration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
	}

	@Override
	public boolean start() throws Exception {
		if (this.configuration == null)
			throw new NullPointerException("configuration");

		BotLogger.setLevel(Level.ALL);
		BotLogger.registerLogger(new ConsoleHandler());

		this.telegramBotsApi = new TelegramBotsApi();

		try {
			this.telegramBotsApi.registerBot(new TorrentAdBot());
		} catch (TelegramApiException e) {
			logger.error(null, e);
			return false;
		}

		return true;
	}

	@Override
	public void stop() {
		// @@@@@ 서비스 중지가 안되는것 같음, 프로세스가 안 떨어짐
//        // @@@@@
//        Thread.sleep(1000);
//        app.stop(); 봇 내부의 private final ExecutorService exe = Executors.newSingleThreadExecutor(); 이거 때문인거 같음

		this.telegramBotsApi = null;
	}

}
