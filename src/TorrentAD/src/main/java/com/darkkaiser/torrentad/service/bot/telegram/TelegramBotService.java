package com.darkkaiser.torrentad.service.bot.telegram;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import com.darkkaiser.torrentad.service.bot.BotService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Objects;

@Slf4j
public class TelegramBotService implements BotService {

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	private final FileTransmissionExecutorService fileTransmissionExecutorService;

	private TelegramBotsApi botsApi;

	private TorrentBot torrentBot;
	
	private BotSession torrentBotSession;

	private final Configuration configuration;

	public TelegramBotService(final ImmediatelyTaskExecutorService immediatelyTaskExecutorService, final FileTransmissionExecutorService fileTransmissionExecutorService, final Configuration configuration) {
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");
		Objects.requireNonNull(fileTransmissionExecutorService, "fileTransmissionExecutorService");
		Objects.requireNonNull(configuration, "configuration");

		this.configuration = configuration;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
		this.fileTransmissionExecutorService = fileTransmissionExecutorService;
	}

	@Override
	public boolean start() throws Exception {
		this.botsApi = new TelegramBotsApi(DefaultBotSession.class);

		try {
			this.torrentBot = new TorrentBot(this.immediatelyTaskExecutorService, this.fileTransmissionExecutorService, this.configuration);
			this.torrentBotSession = this.botsApi.registerBot(this.torrentBot);
		} catch (final TelegramApiException e) {
			log.error(null, e);
			return false;
		}

		return true;
	}

	@Override
	public void stop() {
		if (this.torrentBotSession != null) {
			this.torrentBotSession.stop();
			this.torrentBotSession = null;
		}

		if (this.torrentBot != null) {
			this.torrentBot.dispose();
			this.torrentBot = null;
		}

		this.botsApi = null;
	}

}
