package com.darkkaiser.torrentad;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.config.DefaultConfiguration;
import com.darkkaiser.torrentad.service.Service;
import com.darkkaiser.torrentad.service.ad.TorrentAdService;
import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.au.TorrentAuService;
import com.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.TelegramBotService;
import com.darkkaiser.torrentad.util.notifyapi.NotifyApiClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
	
	private Service torrentAdService;
	private Service torrentAuService;
	private Service torrentBotService;

	private Configuration configuration;

	private boolean start() {
		// 기본 환경설정 정보를 읽어들인다.
		Configuration configuration;
		try {
			configuration = new DefaultConfiguration();
		} catch (final Exception e) {
			log.error(null, e);
			return false;
		}

		this.configuration = configuration;

		// NotifyAPI 클라이언트 객체를 초기화한다.
		NotifyApiClient.init(configuration.getValue(Constants.APP_CONFIG_TAG_NOTIFYAPI_URL), configuration.getValue(Constants.APP_CONFIG_TAG_NOTIFYAPI_APP_KEY), configuration.getValue(Constants.APP_CONFIG_TAG_NOTIFYAPI_APPLICATION_ID));

		try {
			this.torrentAuService = new TorrentAuService(configuration);
			this.torrentAdService = new TorrentAdService(configuration);
			this.torrentBotService = new TelegramBotService((ImmediatelyTaskExecutorService) this.torrentAdService, (FileTransmissionExecutorService) this.torrentAuService, configuration);

			return this.torrentAuService.start() && this.torrentAdService.start() && this.torrentBotService.start();
		} catch (final Exception e) {
			log.error(null, e);
			return false;
		}
	}

	private void stop() {
		if (this.torrentBotService != null)
			this.torrentBotService.stop();
		if (this.torrentAuService != null)
			this.torrentAuService.stop();
		if (this.torrentAdService != null)
			this.torrentAdService.stop();
		if (this.configuration != null)
			this.configuration.dispose();

		this.configuration = null;
		this.torrentAdService = null;
		this.torrentAuService = null;
		this.torrentBotService = null;
	}

    private void addShutdownHook(final App app) {
        Runnable shutdownHook = () -> {
			log.info("{} 프로그램을 종료하는 중입니다...", Constants.APP_NAME);

            app.stop();

			log.info("{} 프로그램이 종료되었습니다.", Constants.APP_NAME);
        };

        // add shutdown hook
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(shutdownHook));
    }
    
	public static void main(final String[] args) {
		try {
			App app = new App();

			String productMessage = "\n" +
					"########################################################\n" +
					"###                                                  ###\n" +
					"###             " + Constants.APP_NAME + " Version " + Constants.APP_VERSION + "              ###\n" +
					"###                                                  ###\n" +
					"###                         developed by DarkKaiser  ###\n" +
					"###                                                  ###\n" +
					"########################################################\n";
			log.info(productMessage);

			if (app.start() == true) {
				log.info("{} 프로그램이 시작되었습니다.", Constants.APP_NAME);

                // add shutdown hook if possible
                app.addShutdownHook(app);
            } else {
                app.stop();

				log.error("{} 프로그램이 종료되었습니다.", Constants.APP_NAME);
            }
		} catch (final Exception e) {
			log.error("{}가 종료되었습니다.", Constants.APP_NAME, e);
		}
	}

}
