package kr.co.darkkaiser.torrentad;

import com.google.gson.Gson;

public class App {
	public static void main(String[] args) {
		Gson gson = new Gson();
//		String json = "{'name':'김태희', 'phone_number':'010-1234-5678'}";
//		Person java = gson.fromJson(json, Person.class);
		  
		System.out.println("Hello World!");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("shutdown");
			}
		});
	}
}
