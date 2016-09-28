package kr.co.darkkaiser.torrentad.service.bot.telegrambots;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.bot.BotService;
import kr.co.darkkaiser.torrentad.service.bot.telegrambots.bots.TelegramTorrentBot;

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
			BotSession registerBot = this.telegramBotsApi.registerBot(new TelegramTorrentBot());
//			registerBot.close();@@@@@ 했을때 제대로 종료되는지 확인해보기, stop에서 적용
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
