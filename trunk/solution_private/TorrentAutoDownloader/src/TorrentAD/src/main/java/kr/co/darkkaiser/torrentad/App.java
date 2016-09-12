package kr.co.darkkaiser.torrentad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.config.DefaultConfiguration;
import kr.co.darkkaiser.torrentad.service.ad.TorrentAdService;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.TorrentSupervisoryControlService;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class App {
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	private TorrentAdService torrentAdService;

	private TorrentSupervisoryControlService torrentSupervisoryControlService;

	private Configuration configuration;

	private boolean start() {
		// 기본 환경설정 정보를 읽어들인다.
		Configuration configuration = null;
		try {
			configuration = new DefaultConfiguration();
		} catch (Exception e) {
			return false;
		}

		this.configuration = configuration;
		
		try {
			AES256Util aes256 = new AES256Util();
			this.torrentSupervisoryControlService = new TorrentSupervisoryControlService(aes256, configuration);
			this.torrentAdService = new TorrentAdService(aes256, configuration);
			return this.torrentSupervisoryControlService.start() && this.torrentAdService.start();
		} catch (Exception e) {
			logger.error(null, e);
			return false;
		}
	}

	private void stop() {
		if (this.torrentSupervisoryControlService != null) {
			this.torrentSupervisoryControlService.stop();
		}
		if (this.torrentAdService != null) {
			this.torrentAdService.stop();
		}
		if (this.configuration != null) {
			this.configuration.dispose();
		}
		
		this.torrentAdService = null;
		this.configuration = null;
	}

    private void addShutdownHook(final App app) {
        Runnable shutdownHook = new Runnable() {
            public void run() {
            	logger.info("{} 프로그램을 종료하는 중입니다...", Constants.APP_NAME);
                app.stop();
            }
        };

        // add shutdown hook
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(shutdownHook));
    }
    
	public static void main(String[] args) {
		try {
			App app = new App();
			if (app != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("\n")
				  .append("########################################################\n")
				  .append("###                                                  ###\n")
				  .append("###             ").append(Constants.APP_NAME).append(" Version ").append(Constants.APP_VERSION).append("              ###\n")
				  .append("###                                                  ###\n")
				  .append("###                         developed by DarkKaiser  ###\n")
				  .append("###                                                  ###\n")
				  .append("########################################################\n");
				logger.info(sb.toString());

				if (app.start() == true) {
					logger.info("{} 프로그램이 시작되었습니다.", Constants.APP_NAME);

		            // add shutdown hook if possible
		            app.addShutdownHook(app);
				} else {
					app.stop();

					logger.error("{} 프로그램이 종료되었습니다.", Constants.APP_NAME);
				}
			}
		} catch (Exception e) {
			logger.error("{}가 종료되었습니다.", Constants.APP_NAME, e);
		}
	}

}
