package kr.co.darkkaiser.torrentad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.overwatch.OverWatcher;

public class App {
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	private OverWatcher overWatcher;

	private boolean start(String configFileName) {
		this.overWatcher = new OverWatcher(configFileName);
		return this.overWatcher.start();
	}

	private void stop() {
		if (this.overWatcher != null) {
			this.overWatcher.stop();
		}
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
				  .append("###             Developed by DarkKaiser              ###\n")
				  .append("###                                                  ###\n")
				  .append("########################################################\n");
				logger.info(sb.toString());

				// start the server
				if (app.start(Constants.APP_CONFIG_FILE_NAME) == true) {
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
