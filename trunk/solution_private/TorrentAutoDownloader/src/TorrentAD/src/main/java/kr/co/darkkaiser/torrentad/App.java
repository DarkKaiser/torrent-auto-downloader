package kr.co.darkkaiser.torrentad;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import kr.co.darkkaiser.torrentad.config.Setting;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;

public class App {
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
    private void addShutdownHook(final App app) {
//        Runnable shutdownHook = new Runnable() {
//            public void run() {
//            	logger.info("FBISDispatcher 서버를 종료하는 중입니다...");
//                app.stop();
//            }
//        };
//
//        // add shutdown hook
//        Runtime runtime = Runtime.getRuntime();
//        runtime.addShutdownHook(new Thread(shutdownHook));
    }
    
	public static void main(String[] args) {
		App main = new App();
//		// 환경설정 파일명을 읽어들인다.
//		String configFilePath = Constants.DISPATCHER_SERVER_CONFIG_FILEPATH;
//		if (args.length > 0) {
//			configFilePath = args[0];
//		}
//
//		try {
//			FbisDispatcherServer server = new FbisDispatcherServer();
//			if (server != null) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("\n")
//				  .append("##################################################\n")
//				  .append("###                                            ###\n")
//				  .append("###       FBISDispatcher 서버 Version 0.1       ###\n")
//				  .append("###                                            ###\n")
//				  .append("###              developed by DarkKaiser       ###\n")
//				  .append("###                                            ###\n")
//				  .append("##################################################\n");
//				logger.info(sb.toString());
//
//				// start the server
//				if (server.start(configFilePath) == true) {
//					logger.info("FBISDispatcher 서버가 시작되었습니다.");
//
//		            // add shutdown hook if possible
//		            main.addShutdownHook(server);
//				} else {
//					server.stop();
//
//					logger.info("FBISDispatcher 서버가 종료되었습니다.");
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//			logger.info("FBISDispatcher 서버가 종료되었습니다.", e);
//		}
		/////////////////////////////////////////////////////////////////////
		
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
		
		System.out.println("Hello World!");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("shutdown");
			}
		});
	}
	
}
