package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BogoNetWebSite extends AbstractWebSite<BogoNetWebSite> {
	
	private static final Logger logger = LoggerFactory.getLogger(BogoNetWebSite.class);

	private static final String LOGIN_URL2 = "https://zipbogo.net/cdsb/login_process.php";
	private static final String LOGIN_URL3 = "https://mybogo.net/cdsb/login_process_extern.php";
	
	// @@@@@ 도메인은 ㅈ거
	// 전체 URL로 표현
	
	protected Connection.Response conn;

	public BogoNetWebSite() {
	}

	@Override
	public void login(WebSiteAccount account) throws IOException {
		if (this.valid() == false) {
			throw new NullPointerException();
		}
		
		// @@@@@
		Connection.Response loginForm = Jsoup.connect(LOGIN_URL2)
				.userAgent("Mozilla")
				.data("mode", "login")
				.data("kinds", "outlogin")
				.data("user_id", account.id())
				.data("passwd", account.password())
                .method(Connection.Method.POST)
                .execute();

		Document loginFormDoc = loginForm.parse();
		String result = loginFormDoc.toString();
		if (result.contains("로그인하셨습니다.\");") == false) {
			return;
		}

		try {
		 Document document2 = Jsoup.connect(loginFormDoc.select("img").attr("src"))
				 	.userAgent("Mozilla")
	                .cookies(loginForm.cookies())
	                .get();
		 	System.out.println(document2);
		} catch (Exception e) {
			
		}

		 Document document = Jsoup.connect("https://mybogo.net/cdsb/login_process_extern.php")
				 	.userAgent("Mozilla")
	                .data("MEMBER_NAME", loginFormDoc.select("input[name=MEMBER_NAME]").val())
	                .data("MEMBER_POINT", loginFormDoc.select("input[name=MEMBER_POINT]").val())
	                .data("STR", loginFormDoc.select("input[name=STR]").val())
	                .data("todo", loginFormDoc.select("input[name=todo]").val())
	                .cookies(loginForm.cookies())
	                .post();

		this.conn = null;
		this.setAccount(account);

		// throw
//		LOGGER.error("異쒕젰�뙆�씪 �깮�꽦�떎�뙣(VM�뙆�씪={}, �깮�꽦�뙆�씪={})", vmFileClassPath, outputFileAbsolutePath);

		System.out.println(loginFormDoc);
	}

	@Override
	public void logout() {
		// @@@@@
		this.conn = null;
	}

	public void search() {
		// @@@@@
		if (this.conn == null) {
			
		}
		

//		 // 게시판이동
//			Connection.Response loginForm2 = Jsoup.connect("https://zipbogo.net/cdsb/board.php?board=newmovie")
//					.userAgent("Mozilla")
//	                .method(Connection.Method.GET)
//	                .cookies(loginForm.cookies())
//	                .execute();

	}

	public void download() {
		// @@@@@
	}

	public void upload() {
		// @@@@@
	}

}
