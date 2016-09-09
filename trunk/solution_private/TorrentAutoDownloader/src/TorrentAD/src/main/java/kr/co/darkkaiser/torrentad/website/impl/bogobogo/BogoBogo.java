package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import kr.co.darkkaiser.torrentad.website.AbstractWebSite;
import kr.co.darkkaiser.torrentad.website.IncorrectLoginAccountException;
import kr.co.darkkaiser.torrentad.website.UnknownLoginException;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;

public class BogoBogo extends AbstractWebSite {

	private static final Logger logger = LoggerFactory.getLogger(BogoBogo.class);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	public static final String BASE_URL = "https://zipbogo.net";
	public static final String BASE_URL_WITH_DEFAULT_PATH = String.format("%s/cdsb", BASE_URL);

	private static final String MAIN_PAGE_URL = BASE_URL;

	private static final String LOGIN_PROCESS_URL_1 = String.format("%s/login_process.php", BASE_URL_WITH_DEFAULT_PATH);
	private static final String LOGIN_PROCESS_URL_2 = "https://mybogo.net/cdsb/login_process_extern.php";

	private static final String DOWNLOAD_PROCESS_URL_1 = String.format("%s/download.php", BASE_URL_WITH_DEFAULT_PATH);
	private static final String DOWNLOAD_PROCESS_URL_2 = "http://linktender.net/";
	private static final String DOWNLOAD_PROCESS_URL_3 = "http://linktender.net/execDownload.php";

	private Connection.Response loginConnResponse;

	private HashMap<BogoBogoBoard, ArrayList<BogoBogoBoardItem>> boardItems = new HashMap<>();

	// 다운로드 받은 파일이 저장되는 위치
	private String downloadFileWriteLocation;

	private final class DownloadProcess1Result {

		private String stat;
		private String key;
		private String msg;
		
		public String getStat() {
			return this.stat;
		}
		
		public String getKey() {
			return this.key;
		}

		public String getMsg() {
			return this.msg;
		}

	}

	public BogoBogo(String downloadFileWriteLocation) {
		super(WebSite.BOGOBOGO);
		
		if (StringUtil.isBlank(downloadFileWriteLocation) == true) {
			throw new IllegalArgumentException("downloadFileWriteLocation은 빈 문자열을 허용하지 않습니다.");
		}
		
		if (downloadFileWriteLocation.endsWith(File.separator) == true) {
			this.downloadFileWriteLocation = downloadFileWriteLocation;
		} else {
			this.downloadFileWriteLocation = String.format("%s%s", downloadFileWriteLocation, File.separator);
		}
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
		} catch (ConnectException e) {
			// @@@@@
		} catch (UnsupportedMimeTypeException e) {
			// 무시한다.
		} catch (IllegalArgumentException e) {
			logger.error("GET {}", doc.select("img").attr("src"));
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

		downloadBoardItemDownloadLink(arrayList.get(0));

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
					throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
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
								throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 1개가 아닙니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}

							String detailPageURL = titleLinkElement.attr("href");
							if (detailPageURL.startsWith("board.php") == false) {
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}

							int noPos = detailPageURL.indexOf("no=");
							if (noPos < 0) {
								throw new ParseException(String.format("게시물의 ID 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}
							String identifier = detailPageURL.substring(noPos + 3/* no= */, detailPageURL.indexOf("&", noPos));

							// 작성자
							iterator.next();

							// 날짜
							String registDate = iterator.next().text().trim();

							boardItems.add(new BogoBogoBoardItem(board, Long.parseLong(identifier), title, registDate, String.format("%s/%s", BogoBogo.BASE_URL_WITH_DEFAULT_PATH, detailPageURL)));
						}
					} catch (NoSuchElementException e) {
						logger.error(String.format("게시물을 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, elements.html()), e);
						throw e;
					}
				}
			}
		} catch (NoSuchElementException e) {
			// 아무 처리도 하지 않는다.
			return false;
		} catch (ParseException e) {
			logger.error("게시판({}) 데이터를 로드하는 중에 예외가 발생하였습니다.", board, e);
			return false;
		} catch (Exception e) {
			logger.error("게시판({}) 데이터를 로드하는 중에 예외가 발생하였습니다.", board, e);
			return false;
		}

		this.boardItems.put(board, boardItems);

		return true;
	}

	private boolean loadBoardItemDownloadLink(BogoBogoBoardItem boardItem) {
		assert boardItem != null;
		assert isLogin() == true;

		String detailPageURL = boardItem.getDetailPageURL();
		if (StringUtil.isBlank(detailPageURL) == true) {
			logger.error(String.format("게시물의 상세페이지 URL이 빈 문자열이므로, 첨부파일에 대한 정보를 로드할 수 없습니다.(%s)", boardItem));
			return false;
		}

		boardItem.clearDownloadLink();

		try {
			Connection.Response detailPageResponse = Jsoup.connect(detailPageURL)
					.userAgent(USER_AGENT)
	                .method(Connection.Method.GET)
	                .cookies(this.loginConnResponse.cookies())
	                .execute();

			if (detailPageResponse.statusCode() != 200) {
				throw new IOException("GET " + detailPageURL + " returned " + detailPageResponse.statusCode() + ": " + detailPageResponse.statusMessage());
			}

			Document detailPageDoc = detailPageResponse.parse();
			Elements elements = detailPageDoc.select("table.board01 tbody.num tr a[id^='downLink_num']");

			if (elements.isEmpty() == true) {
				throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
			} else {
				try {
					String[] exceptFileExtension = { "JPG", "JPEG", "GIF", "PNG" };

					for (Element element : elements) {
						String id = element.attr("id");
						String value1 = element.attr("val");
						String value2 = element.attr("val2");
						String value3 = element.attr("val3");
						String value4 = element.attr("val4");
						String fileId = element.attr("file_id");
						String fileName = element.text();

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtension).contains(value4.toUpperCase()) == true) {
							continue;
						}

						boardItem.addDownloadLink(DefaultBogoBogoBoardItemDownloadLink.newInstance(id, value1, value2, value3, value4, fileId, fileName));
					}
				} catch (NoSuchElementException e) {
					logger.error(String.format("게시물에서 첨부파일에 대한 정보를 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", detailPageURL, elements.html()), e);
					throw e;
				}
			}
		} catch (NoSuchElementException e) {
			// 아무 처리도 하지 않는다.
			return false;
		} catch (ParseException e) {
			logger.error("게시물({})의 첨부파일에 대한 정보를 로드하는 중에 예외가 발생하였습니다.", boardItem, e);
			return false;
		} catch (Exception e) {
			logger.error("게시물({})의 첨부파일에 대한 정보를 로드하는 중에 예외가 발생하였습니다.", boardItem, e);
			return false;
		}
		
		return true;
	}

	private boolean downloadBoardItemDownloadLink(BogoBogoBoardItem boardItem) {
		assert boardItem != null;
		assert isLogin() == true;

		String detailPageURL = boardItem.getDetailPageURL();
		assert StringUtil.isBlank(detailPageURL) == false;

		Gson gson = new GsonBuilder().create();
		
		Iterator<BogoBogoBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			BogoBogoBoardItemDownloadLink downloadLink = iterator.next();

			try {
				/**
				 * 다운로드 페이지로 이동
				 */
				Connection.Response downloadProcess1Response = Jsoup.connect(DOWNLOAD_PROCESS_URL_1)
						.userAgent(USER_AGENT)
						.header("Referer", detailPageURL)
		                .data("down", downloadLink.getValue1())
		                .data("filetype", downloadLink.getValue4())
		                .data("file_id", downloadLink.getFileId())
		                .data("article_id", downloadLink.getId())
		                .method(Connection.Method.POST)
		                .cookies(this.loginConnResponse.cookies())
		                .ignoreContentType(true)
		                .execute();

				if (downloadProcess1Response.statusCode() != 200) {
					throw new IOException("POST " + DOWNLOAD_PROCESS_URL_1 + " returned " + downloadProcess1Response.statusCode() + ": " + downloadProcess1Response.statusMessage());
				}

				String result = downloadProcess1Response.parse().body().html();
				DownloadProcess1Result downloadProcess1Result = gson.fromJson(result, DownloadProcess1Result.class);
				if (StringUtil.isBlank(downloadProcess1Result.getStat()) == true ||
						StringUtil.isBlank(downloadProcess1Result.getKey()) == true ||
						downloadProcess1Result.getStat().equals("true") == false) {
					throw new JsonParseException(String.format("첨부파일을 다운로드 하기 위한 작업 진행중에 수신된 데이터의 값이 유효하지 않습니다.(%s)", result));
				}

				/**
				 * 다운로드 링크 페이지 열기
				 */
				Connection.Response downloadProcess2Response = Jsoup.connect(String.format("%s%s", DOWNLOAD_PROCESS_URL_2, downloadProcess1Result.getKey()))
						.userAgent(USER_AGENT)
						.data("dddd", downloadLink.getValue1())
		                .data("vvvv", downloadLink.getValue2())
		                .data("ssss", downloadLink.getValue3())
		                .data("code", downloadProcess1Result.getKey())
		                .data("file_id", downloadLink.getFileId())
		                .data("valid_id", downloadProcess1Result.getMsg())
		                .method(Connection.Method.POST)
		                .cookies(this.loginConnResponse.cookies())
		                .execute();
				
				if (downloadProcess2Response.statusCode() != 200) {
					throw new IOException("POST " + String.format("%s%s", DOWNLOAD_PROCESS_URL_2, downloadProcess1Result.getKey()) + " returned " + downloadProcess2Response.statusCode() + ": " + downloadProcess2Response.statusMessage());
				}

				Document downloadProcess2Doc = downloadProcess2Response.parse();
				
				// @@@@@ 첨부파일 명 구하기
				///////////////////////////////////////////////////////////
				// @@@@@ $("#fileDetail")
				Elements elements = downloadProcess2Doc.select("#fileDetail p");
				
//				elementsByTag.size() == 3;
				Element element2 = elements.get(1);
				String text = element2.text();
				text = text.replace("Filename:", "").trim();

				// @@@@@ 동일파일이 존재할경우에는??
				String filePath = String.format("%s%s", this.downloadFileWriteLocation, text);
				///////////////////////////////////////////////////////////

				/**
				 * 첨부파일 다운로드 하기
				 */
				Connection.Response downloadProcess3Response = Jsoup.connect(DOWNLOAD_PROCESS_URL_3)
						.userAgent(USER_AGENT)
						.header("Referer", String.format("%s%s", DOWNLOAD_PROCESS_URL_2, downloadProcess1Result.getKey()))
		                .data("dddd", downloadProcess2Doc.select("input[id=dddd]").val())
		                .data("vvvv", downloadProcess2Doc.select("input[id=vvvv]").val())
		                .data("file_id", downloadProcess2Doc.select("input[id=file_id]").val())
		                .data("valid_id", downloadProcess2Doc.select("input[id=valid_id]").val())
		                .method(Connection.Method.POST)
		                .cookies(this.loginConnResponse.cookies())
		                .ignoreContentType(true)
		                .execute();

				if (downloadProcess3Response.statusCode() != 200) {
					throw new IOException("POST " + DOWNLOAD_PROCESS_URL_3 + " returned " + downloadProcess3Response.statusCode() + ": " + downloadProcess3Response.statusMessage());
				}
				
				if (downloadProcess3Response.parse().text().contains("Unauthorized Access") == true) {
//					Unauthorized Access 문자가 잇으면 예외발생 시키기 @@@@@
					System.out.println("############################# unauthorized access");
					throw new Exception();
				}

				/**
				 * 첨부파일 저장
				 */
				// @@@@@ 검토
				FileOutputStream fos = new FileOutputStream(new File(filePath));
				fos.write(downloadProcess3Response.bodyAsBytes());
				fos.close();

				// @@@@@ 인증실패라고 뜨는 경우가 있음
				System.out.println(downloadProcess3Response.parse());
			} catch (Exception e) {
				logger.error(String.format("첨부파일 다운로드 중에 예외가 발생하였습니다.(%s, %s)", boardItem, downloadLink), e);
			}
		}

		// @@@@@ 반환값 false는 없음
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogo.class.getSimpleName())
				.append("{")
				.append("로그인 여부:").append(isLogin())
				.append(", 다운로드파일 저장위치:").append(this.downloadFileWriteLocation)
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
