package kr.co.darkkaiser.torrentad;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Setting;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;

public class App {
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private boolean start(String configFilePath) {
		// @@@@@
		Gson gson = new Gson();
		String json = "{'domain':'zipbogo.net', 'phone_number':'010-1234-5678'}";

		Setting obj = gson.fromJson(json, Setting.class);

//		Person java = gson.fromJson(json, Person.class);
		
		BogoBogoWebSite l = new BogoBogoWebSite();

		try {
			l.login(new BogoBogoWebSiteAccount("darkkaiser", "DreamWakuWaku78@"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* 반환 */ l.search(/* 검색정보 */);
		/* 반환받은 정보를 이용해서 다운로드 */
		/* 결과정보*/l.download(/*다운로드정보*/);
		l.upload(/*결과정보*/);
		
		l.logout();
		
		return true;
	}
	
	private void stop() {
		// @@@@@
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
				if (app.start(Constants.DISPATCHER_SERVER_CONFIG_FILEPATH) == true) {
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
