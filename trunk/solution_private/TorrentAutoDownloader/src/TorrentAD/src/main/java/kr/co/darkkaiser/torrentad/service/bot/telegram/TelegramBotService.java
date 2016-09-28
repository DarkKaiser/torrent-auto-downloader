package kr.co.darkkaiser.torrentad.service.bot.telegram;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.bot.BotService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.bots.TelegramTorrentBot;

public class TelegramBotService implements BotService {

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
			this.telegramBotsApi.registerBot(new TelegramTorrentBot());
		} catch (TelegramApiException e) {
			logger.error(null, e);
			return false;
		}

		return true;
	}

	@Override
	public void stop() {
		this.telegramBotsApi = null;
	}

}
