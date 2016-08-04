package kr.co.darkkaiser.torrentad;

import java.io.IOException;

import com.google.gson.Gson;

import kr.co.darkkaiser.torrentad.website.BogoNetWebSite;

public class App {
	public static void main(String[] args) {
		Gson gson = new Gson();
//		String json = "{'name':'김태희', 'phone_number':'010-1234-5678'}";
//		Person java = gson.fromJson(json, Person.class);
		
		BogoNetWebSite l = new BogoNetWebSite();

		l.account("darkkaiser", "DreamWakuWaku78@");
		l.domain("zipbogo.net");
		
		System.out.println(l);
		
		try {
			l.login();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
