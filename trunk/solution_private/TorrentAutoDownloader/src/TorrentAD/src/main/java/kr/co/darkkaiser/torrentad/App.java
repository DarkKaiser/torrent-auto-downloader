package kr.co.darkkaiser.torrentad;

import java.io.IOException;

import com.google.gson.Gson;

import kr.co.darkkaiser.torrentad.config.Setting;
import kr.co.darkkaiser.torrentad.website.BogoNetWebSiteAccount;
import kr.co.darkkaiser.torrentad.website.BogoNetWebSite;

public class App {
	public static void main(String[] args) {
		Gson gson = new Gson();
		String json = "{'domain':'zipbogo.net', 'phone_number':'010-1234-5678'}";

		Setting obj = gson.fromJson(json, Setting.class);

//		Person java = gson.fromJson(json, Person.class);
		
		BogoNetWebSite l = new BogoNetWebSite();

		try {
			l.login(new BogoNetWebSiteAccount("darkkaiser", "DreamWakuWaku78@"));
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
