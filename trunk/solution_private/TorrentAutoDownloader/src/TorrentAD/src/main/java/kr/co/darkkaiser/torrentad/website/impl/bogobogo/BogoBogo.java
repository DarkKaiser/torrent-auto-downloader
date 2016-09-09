package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.website.AbstractWebSite;
import kr.co.darkkaiser.torrentad.website.IncorrectLoginAccountException;
import kr.co.darkkaiser.torrentad.website.UnknownLoginException;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.board.WebSiteBoardItem;

public class BogoBogo extends AbstractWebSite {
	
	private static final Logger logger = LoggerFactory.getLogger(BogoBogo.class);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	public static final String BASE_URL = "https://zipbogo.net";
	public static final String BASE_URL_WITH_PATH = String.format("%s/cdsb", BASE_URL);

	private static final String MAIN_PAGE_URL = BASE_URL;
	private static final String LOGIN_PROCESS_URL_1 = String.format("%s/login_process.php", BASE_URL_WITH_PATH);
	private static final String LOGIN_PROCESS_URL_2 = "https://mybogo.net/cdsb/login_process_extern.php";

	protected Connection.Response loginConnResponse;

	protected HashMap<BogoBogoBoard, ArrayList<BogoBogoBoardItem>> boardItems = new HashMap<>();

	public BogoBogo() {
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
	public Iterator<WebSiteBoardItem> search(WebSiteSearchContext searchContext) throws Exception {
		if (searchContext == null) {
			throw new NullPointerException("searchContext");
		}

		if (isLogin() == false) {
			throw new IllegalStateException("로그인 상태가 아닙니다.");
		}

		BogoBogoSearchContext siteSearchContext = (BogoBogoSearchContext) searchContext;

		// @@@@@
		//////////////////////////////////////////////////////////////////////
		if (loadBoardItems(siteSearchContext.getBoard()) == false) {
			return null;
		}
		
		ArrayList<BogoBogoBoardItem> arrayList = this.boardItems.get(siteSearchContext.getBoard());
		loadBoardItemDownloadLink(arrayList.get(0));

		return null;
	}
	
	private boolean loadBoardItems(BogoBogoBoard board) {
		assert board != null;
		assert isLogin() == true;

		if (this.boardItems.containsKey(board) == true) {
			return true;
		}

		ArrayList<BogoBogoBoardItem> boardItems = new ArrayList<>();

		try {
			for (int pageNo = 1; pageNo <= board.getDefaultLoadPageCount(); ++pageNo) {
				String url = String.format("%s&page=%s", board.getURL(), pageNo);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .cookies(this.loginConnResponse.cookies())
		                .execute();
	
				if (boardItemsResponse.statusCode() != 200) {
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());
				}
	
				Document boardItemsDoc = boardItemsResponse.parse();
				Elements elements = boardItemsDoc.select("table.board01 tbody.num tr");
				
				if (elements.isEmpty() == true) {
					throw new ParseException(String.format("추출된 게시판의 자료가 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
				} else {
					try {
						for (Element element : elements) {
							Iterator<Element> iterator = element.getElementsByTag("td").iterator();

							// 번호
							if (iterator.next().text().contains("[공지]") == true) {
								continue;
							}

							// 카테고리
							iterator.next();

							// 제목
							Element titleElement = iterator.next();
							String title = titleElement.text().trim();
							if (title.contains("신고에의해 블라인드 된 글입니다.") == true) {
								continue;
							}
							
							Elements titleLinkElement = titleElement.getElementsByTag("a");
							if (titleLinkElement.size() != 1) {
								throw new ParseException(String.format("게시판 제목 항목의 <A> 태그가 1개가 아닙니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}

							String detailPageURL = titleLinkElement.attr("href");
							if (detailPageURL.startsWith("board.php") == false) {
								throw new ParseException(String.format("게시판 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}

							int noPos = detailPageURL.indexOf("no=");
							if (noPos < 0) {
								throw new ParseException(String.format("게시판 항목의 ID 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}
							String identifier = detailPageURL.substring(noPos + 3/* no= */, detailPageURL.indexOf("&", noPos));

							// 작성자
							iterator.next();

							// 날짜
							String registDate = iterator.next().text().trim();

							boardItems.add(new BogoBogoBoardItem(board, Long.parseLong(identifier), title, registDate, String.format("%s/%s", BogoBogo.BASE_URL_WITH_PATH, detailPageURL)));
						}
					} catch (NoSuchElementException e) {
						logger.error(String.format("게시판의 항목을 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, elements.html()), e);
						throw e;
					}
				}
			}
		} catch (NoSuchElementException e) {
			// 아무 처리도 하지 않는다.
			return false;
		} catch (ParseException e) {
			logger.error("게시판 자료를 읽어들이는 중에 예외가 발생하였습니다.", e);
			return false;
		} catch (Exception e) {
			logger.error("게시판 자료를 읽어들이는 중에 예외가 발생하였습니다.", e);
			return false;
		}

		this.boardItems.put(board, boardItems);

		return true;
	}

	// @@@@@
	private boolean loadBoardItemDownloadLink(BogoBogoBoardItem boardItem) {
		// $("table.board01 tbody.num tr a[id^='downLink_num']")
		System.out.println("#########$$$$$$$$$$$$$$$");
		return true;
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogo.class.getSimpleName())
				.append("{")
				.append("로그인 여부:").append(isLogin())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
