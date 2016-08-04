package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BogoBogoWebSite extends AbstractWebSite<BogoBogoWebSite> {
	
	private static final Logger logger = LoggerFactory.getLogger(BogoBogoWebSite.class);
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	private static final String LOGIN_PROCESS_URL_1 = "https://zipbogo.net/cdsb/login_process.php";
	private static final String LOGIN_PROCESS_URL_2 = "https://mybogo.net/cdsb/login_process_extern.php";

	protected Connection.Response conn;

	public BogoBogoWebSite() {
	}

	@Override
	public void login(WebSiteAccount account) throws IOException {
		logout();

		if (account == null) {
			throw new NullPointerException("account");
		}

		if (account.valid() == false) {
			throw new InvalidWebSiteAccountException(account.toString());
		}

		// 보고보고넷 로그인
		Connection.Response response = Jsoup.connect(LOGIN_PROCESS_URL_1)
			.userAgent(USER_AGENT)
			.data("mode", "login")
			.data("kinds", "outlogin")
			.data("user_id", account.id())
			.data("passwd", account.password())
			.method(Connection.Method.POST)
			.execute();

		if (response.statusCode() != 200) {
			throw new IOException("POST " + LOGIN_PROCESS_URL_1 + " returned " + response.statusCode() + ": " + response.statusMessage());
		}

		////////////////////////////////////////////////////////////////////////////
		// @@@@@
		// 로그인이 정상적으로 수행되었는지 확인한다.
		Document doc = response.parse();
		System.out.println(doc.outerHtml());
		if (doc.outerHtml().contains("로그인하셨습니다.\");") == false) {
			return;
//			alert("편진휴님 로그인하셨습니다.");
		}
		// 비밀번호 틀리면 익셉션 발생

		try {
		 Document document2 = Jsoup.connect(doc.select("img").attr("src"))
				 	.userAgent(USER_AGENT)
	                .cookies(response.cookies())
	                .get();
		 	System.out.println(document2);
		} catch (Exception e) {
			
		}

		 Document document = Jsoup.connect(LOGIN_PROCESS_URL_2)
				 	.userAgent(USER_AGENT)
	                .data("MEMBER_NAME", doc.select("input[name=MEMBER_NAME]").val())
	                .data("MEMBER_POINT", doc.select("input[name=MEMBER_POINT]").val())
	                .data("STR", doc.select("input[name=STR]").val())
	                .data("todo", doc.select("input[name=todo]").val())
	                .cookies(response.cookies())
	                .post();

		this.conn = null;
		this.setAccount(account);

		// throw
//		LOGGER.error("異쒕젰�뙆�씪 �깮�꽦�떎�뙣(VM�뙆�씪={}, �깮�꽦�뙆�씪={})", vmFileClassPath, outputFileAbsolutePath);

		System.out.println(doc);
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
