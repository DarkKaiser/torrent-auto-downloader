package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.website.AbstractWebSite;
import kr.co.darkkaiser.torrentad.website.IncorrectLoginAccountException;
import kr.co.darkkaiser.torrentad.website.UnknownLoginException;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;

public class BogoBogoWebSite extends AbstractWebSite {
	
	private static final Logger logger = LoggerFactory.getLogger(BogoBogoWebSite.class);
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	private static final String MAIN_PAGE_URL = "https://zipbogo.net/";
	private static final String LOGIN_PROCESS_URL_1 = "https://zipbogo.net/cdsb/login_process.php";
	private static final String LOGIN_PROCESS_URL_2 = "https://mybogo.net/cdsb/login_process_extern.php";

	protected Connection.Response loginConnResponse;

	public BogoBogoWebSite() {
		super(WebSite.BOGOBOGO);
	}
	
	@Override
	protected void login0(WebSiteAccount account) throws Exception {
		if (account == null) {
			throw new NullPointerException("account");
		}

		account.validate();

		/**
		 * 로그인 1단계 수행
		 */
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

		// 로그인이 정상적으로 완료되었는지 확인한다.
		Document doc = response.parse();
		String outerHtml = doc.outerHtml();
		if (outerHtml.contains("님 로그인하셨습니다.\");") == false) {		// 'alert("xxx님 로그인하셨습니다.");' 문자열이 포함되어있는지 확인
			// 'alert("로그인 정보가 x회 틀렸습니다.\n(5회이상 틀렸을시 30분동안 로그인 하실수 없습니다.)");' 문자열이 포함되어있는지 확인
			if (outerHtml.contains("회 틀렸습니다.") == true && outerHtml.contains("(5회이상 틀렸을시 30분동안 로그인 하실수 없습니다.)") == true) {
				throw new IncorrectLoginAccountException("POST " + LOGIN_PROCESS_URL_1 + " return message:\n" + outerHtml);
			}

			throw new UnknownLoginException("POST " + LOGIN_PROCESS_URL_1 + " return message:\n" + outerHtml);
		}

		/**
		 * 로그인 2단계 수행
		 */
		try {
			Jsoup.connect(doc.select("img").attr("src"))
				.userAgent(USER_AGENT)
				.cookies(response.cookies())
				.get();
		} catch (UnsupportedMimeTypeException e) {
			// 무시한다.
		} catch (IllegalArgumentException e) {
			logger.error("POST {} return message:\n{}", LOGIN_PROCESS_URL_1, outerHtml);
			throw e;
		}

		/**
		 * 로그인 3단계 수행
		 */
		Jsoup.connect(LOGIN_PROCESS_URL_2)
			.userAgent(USER_AGENT)
			.data("MEMBER_NAME", doc.select("input[name=MEMBER_NAME]").val())
			.data("MEMBER_POINT", doc.select("input[name=MEMBER_POINT]").val())
			.data("STR", doc.select("input[name=STR]").val())
			.data("todo", doc.select("input[name=todo]").val())
			.cookies(response.cookies())
			.post();

		/**
		 * 로그인이 정상적으로 완료되었는지 확인한다.
		 */
		Connection.Response completedCheckResponse = Jsoup.connect(MAIN_PAGE_URL)
			.userAgent(USER_AGENT)
			.method(Connection.Method.GET)
			.cookies(response.cookies())
			.execute();

		if (completedCheckResponse.statusCode() != 200) {
			throw new IOException("GET " + MAIN_PAGE_URL + " returned " + completedCheckResponse.statusCode() + ": " + completedCheckResponse.statusMessage());
		}

		Document completedCheckDoc = completedCheckResponse.parse();
		String completedCheckOuterHtml = completedCheckDoc.outerHtml();
		if (completedCheckOuterHtml.contains("<input type=\"button\" value=\"로그아웃\" id=\"lox\" onclick=\"window.location.href='/cdsb/login_process.php?mode=logout'\">") == false) {
			throw new UnknownLoginException("GET " + MAIN_PAGE_URL + " return message:\n" + completedCheckOuterHtml);
		}

		/**
		 * 로그인 완료 처리 수행
		 */
		setAccount(account);

		this.loginConnResponse = response;
	}
	
	@Override
	protected void logout0() throws Exception {
		setAccount(null);

		this.loginConnResponse = null;
	}

	@Override
	public boolean isLogin() {
		if (getAccount() == null || this.loginConnResponse == null) {
			return false;
		}

		return true;
	}

	@Override
	public void search(WebSiteSearchContext searchContext) {
		// @@@@@
		if (isLogin() == false) {
		}
		

//				 // 게시판이동
//					Connection.Response loginForm2 = Jsoup.connect("https://zipbogo.net/cdsb/board.php?board=newmovie")
//							.userAgent("Mozilla")
//			                .method(Connection.Method.GET)
//			                .cookies(loginForm.cookies())
//			                .execute();
	}

}
