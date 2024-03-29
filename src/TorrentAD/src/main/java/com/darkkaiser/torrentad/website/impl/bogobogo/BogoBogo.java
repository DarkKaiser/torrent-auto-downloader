package com.darkkaiser.torrentad.website.impl.bogobogo;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.website.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;
import java.util.*;

@Slf4j
public class BogoBogo extends AbstractWebSite {

	private static final String BASE_URL = "https://qhrhqhrh.net";
	public static final String BASE_URL_WITH_DEFAULT_PATH = String.format("%s/cdsb", BASE_URL);

	private static final String MAIN_PAGE_URL = BASE_URL;

	private static final String LOGIN_PROCESS_URL_1 = String.format("%s/login_process.php", BASE_URL_WITH_DEFAULT_PATH);
	private static final String LOGIN_PROCESS_URL_2 = "https://mybogo.net/cdsb/login_process_extern.php";
	private static final String LOGIN_PROCESS_URL_3 = "https://mybogo.net/bogo/member_share/sync_sid.php";
	
	private static final String DOWNLOAD_PROCESS_URL_1 = String.format("%s/download.php", BASE_URL_WITH_DEFAULT_PATH);
	private static final String DOWNLOAD_PROCESS_URL_2 = "http://linktender.net/";
	private static final String DOWNLOAD_PROCESS_URL_3 = "http://linktender.net/execDownload.php";

	private Connection.Response loginConnResponse;

	@Getter
	private static final class DownloadProcess1Result {

		private String stat;
		private String key;
		private String msg;

	}

	public BogoBogo(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.BOGOBOGO, downloadFileWriteLocation);
	}
	
	@Override
	protected void login0(final WebSiteAccount account) throws Exception {
		Objects.requireNonNull(account, "account");

		account.validate();

		/*
		  로그인 1단계 수행
		 */
		Connection.Response response = Jsoup.connect(LOGIN_PROCESS_URL_1)
				.userAgent(USER_AGENT)
				.data("mode", "login")
				.data("kinds", "outlogin")
				.data("user_id", account.id())
				.data("passwd", account.password())
				.method(Connection.Method.POST)
				.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
				.execute();

		if (response.statusCode() != HttpStatus.SC_OK)
			throw new IOException("POST " + LOGIN_PROCESS_URL_1 + " returned " + response.statusCode() + ": " + response.statusMessage());

		// 로그인이 정상적으로 완료되었는지 확인한다.
		Document doc = response.parse();
		String outerHtml = doc.outerHtml();
		if (outerHtml.contains("님 로그인하셨습니다.") == false) {		// 'alert(" 편진휴님 로그인하셨습니다.. \n( * 이메일 : darkkaiser78@hotmail.com )");' 문자열이 포함되어있는지 확인
			// 'alert("로그인 정보가 x회 틀렸습니다.\n(5회이상 틀렸을시 30분동안 로그인 하실수 없습니다.)");' 문자열이 포함되어있는지 확인
			if (outerHtml.contains("회 틀렸습니다.") == true && outerHtml.contains("(5회이상 틀렸을시 30분동안 로그인 하실수 없습니다.)") == true)
				throw new IncorrectLoginAccountException("POST " + LOGIN_PROCESS_URL_1 + " return message:\n" + outerHtml);

			throw new UnknownLoginException("POST " + LOGIN_PROCESS_URL_1 + " return message:\n" + outerHtml);
		}

		/*
		  로그인 2단계 수행
		 */
		// 간혹 ConnectException 예외가 발생하므로 루프를 돌린다.
        for (int loopCount = 0; loopCount < 3; ++loopCount) {
			try {
				Jsoup.connect(doc.select("img").attr("src"))
					.userAgent(USER_AGENT)
					.cookies(response.cookies())
					.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
					.ignoreContentType(true)
					.get();
			} catch (final HttpStatusException | ConnectException | IllegalArgumentException e) {
				try {
					Thread.sleep(100);
				} catch (final Exception ignored) {
				}

				continue;
			}

			break;
		}

		/*
		  로그인 3단계 수행
		 */
		try {
			Jsoup.connect(LOGIN_PROCESS_URL_2)
				.userAgent(USER_AGENT)
				.data("MEMBER_NAME", doc.select("input[name=MEMBER_NAME]").val())
				.data("MEMBER_POINT", doc.select("input[name=MEMBER_POINT]").val())
				.data("STR", doc.select("input[name=STR]").val())
				.data("todo", doc.select("input[name=todo]").val())
				.cookies(response.cookies())
				.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
				.post();
		} catch (final HttpStatusException | ConnectException | IllegalArgumentException ignored) {
		}
		
		/*
		  로그인 4단계 수행
		 */
		try {
			Jsoup.connect(LOGIN_PROCESS_URL_3)
				.userAgent(USER_AGENT)
				.data("sid", doc.select("input[name=sid]").val())
				.data("uid", doc.select("input[name=uid]").val())
				.cookies(response.cookies())
				.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
				.post();
		} catch (final HttpStatusException | ConnectException | IllegalArgumentException ignored) {
		}

		/*
		  로그인이 정상적으로 완료되었는지 확인한다.
		 */
		Connection.Response completedCheckResponse = Jsoup.connect(MAIN_PAGE_URL)
				.userAgent(USER_AGENT)
				.method(Connection.Method.GET)
				.cookies(response.cookies())
				.execute();

		if (completedCheckResponse.statusCode() != HttpStatus.SC_OK)
			throw new IOException("GET " + MAIN_PAGE_URL + " returned " + completedCheckResponse.statusCode() + ": " + completedCheckResponse.statusMessage());

		Document completedCheckDoc = completedCheckResponse.parse();
		String completedCheckOuterHtml = completedCheckDoc.outerHtml();
		if (completedCheckOuterHtml.contains("<input type=\"button\" value=\"로그아웃\" id=\"lox\" onclick=\"window.location.href='/cdsb/login_process.php?mode=logout'\">") == false)
			throw new UnknownLoginException("GET " + MAIN_PAGE_URL + " return message:\n" + completedCheckOuterHtml);

		/*
		  로그인 완료 처리 수행
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
		return getAccount() != null && this.loginConnResponse != null;
	}

	@Override
	protected String getSearchQueryString(final String keyword) {
		return String.format("&search=subject&keyword=%s&recom=", keyword);
	}

	@Override
	protected List<WebSiteBoardItem> loadBoardItems0_0(final WebSiteBoard board, final String queryString) throws NoPermissionException {
		assert board != null;
		assert isLogin() == true;

		String _queryString = queryString;
		if (StringUtil.isBlank(_queryString) == true)
			_queryString = "";
		if (_queryString.startsWith("&") == true)
			_queryString = _queryString.substring(1);

		String url = null;
		List<WebSiteBoardItem> boardItems = new ArrayList<>();

		try {
			final BogoBogoBoard siteBoard = (BogoBogoBoard) board;

			for (int page = 1; page <= siteBoard.getDefaultLoadPageCount(); ++page) {
				url = String.format("%s&page=%d&%s", siteBoard.getPath(), page, _queryString);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .cookies(this.loginConnResponse.cookies())
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				Document boardItemsDoc = boardItemsResponse.parse();
				Elements elements = boardItemsDoc.select("table.board01 tbody.num tr");

				if (elements.isEmpty() == true) {
                    //noinspection StatementWithEmptyBody
                    if (boardItemsDoc.html().contains("alert(\"잘못된 접근입니다.\");") == true) {
						// 해당 페이지가 존재하지 않는 경우... 아무 처리도 하지 않는다.
					} else if (boardItemsDoc.html().contains("alert(\"게시판 접근 권한이 없습니다.\");") == true) {
						throw new NoPermissionException(String.format("게시판 접근 권한이 없습니다.(URL:%s)", url));
					} else {
						throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
					}
				} else {
					try {
						for (final Element element : elements) {
							Iterator<Element> iterator = element.getElementsByTag("td").iterator();

							//
							// 번호
							//
							String no = iterator.next().text();
							if (no.contains("[공지]"/* 공지사항 */) == true || no.contains("등록된 글이 없습니다."/* 페이지에 게시물이 0건인 경우... */) == true)
								continue;

							//
							// 카테고리
							//
							if (siteBoard.hasCategory() == true)
								iterator.next();

							//
							// 제목
							//
							Element titleElement = iterator.next();
							String title = titleElement.text();

							// 제목끝에 붙어있는 댓글수('(0)') 문자열을 제거한다.
							if (title.charAt(title.length() - 1) == ')')
								title = title.substring(0, title.lastIndexOf("("));

							title = trimString(title);

							if (title.contains("신고에의해 블라인드 된 글입니다.") == true)
								continue;
							
							Elements titleLinkElement = titleElement.getElementsByTag("a");
							if (titleLinkElement.size() != 1)
								throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 1개가 아닙니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

							String detailPageURL = titleLinkElement.attr("href");
							if (detailPageURL.startsWith("board.php") == false)
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

							int noPos = detailPageURL.indexOf("no=");
							if (noPos < 0)
								throw new ParseException(String.format("게시물의 ID 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							String identifier = detailPageURL.substring(noPos + 3/* no= */, detailPageURL.indexOf("&", noPos));

							//
							// 작성자
							//
							iterator.next();

							//
							// 날짜
							//
							String registDate = iterator.next().text().trim();

							boardItems.add(new DefaultWebSiteBoardItem(siteBoard, Long.parseLong(identifier), title, registDate, String.format("%s/%s", BogoBogo.BASE_URL_WITH_DEFAULT_PATH, detailPageURL)));
						}
					} catch (final NoSuchElementException e) {
						log.error(String.format("게시물을 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, elements.html()), e);
						throw e;
					}
				}
			}
		} catch (final NoPermissionException e) {
			throw e;
		} catch (final NoSuchElementException e) {
			// 아무 처리도 하지 않는다.
			return null;
		} catch (final Exception e) {
			log.error(String.format("게시판(%s) 데이터를 로드하는 중에 예외가 발생하였습니다.(URL:%s)", board, url), e);
			return null;
		}

		boardItems.sort(new WebSiteBoardItemComparatorIdentifierAsc());
		
		return boardItems;
	}

	@Override
	protected boolean loadBoardItemDownloadLink0(final WebSiteBoardItem boardItem) throws NoPermissionException {
		assert boardItem != null;
		assert isLogin() == true;

		String detailPageURL = boardItem.getDetailPageURL();
		if (StringUtil.isBlank(detailPageURL) == true) {
			log.error(String.format("게시물의 상세페이지 URL이 빈 문자열이므로, 첨부파일에 대한 정보를 로드할 수 없습니다.(%s)", boardItem));
			return false;
		}

		boardItem.clearDownloadLink();

		try {
			Connection.Response detailPageResponse = Jsoup.connect(detailPageURL)
					.userAgent(USER_AGENT)
	                .method(Connection.Method.GET)
	                .cookies(this.loginConnResponse.cookies())
	                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
	                .execute();

			if (detailPageResponse.statusCode() != HttpStatus.SC_OK)
				throw new IOException("GET " + detailPageURL + " returned " + detailPageResponse.statusCode() + ": " + detailPageResponse.statusMessage());

			Document detailPageDoc = detailPageResponse.parse();
			Elements elements = detailPageDoc.select("table.board01 tbody.num tr a[id^='downLink_num']");

			if (elements.isEmpty() == true) {
				if (detailPageDoc.html().contains("alert(\"게시판 접근 권한이 없습니다.\");") == true) {
					throw new NoPermissionException(String.format("게시판 접근 권한이 없습니다.(URL:%s)", detailPageURL));
				} else {
					throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
				}
			} else {
				try {
					String[] exceptFileExtension = { "JPG", "JPEG", "GIF", "PNG" };

					for (final Element element : elements) {
						String id = element.attr("id");
						String value1 = element.attr("val");
						String value2 = element.attr("val2");
						String value3 = element.attr("val3");
						String value4 = element.attr("val4");
						String fileId = element.attr("file_id");
						String fileName = trimString(element.text());

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtension).contains(value4.toUpperCase()) == true)
							continue;

						boardItem.addDownloadLink(BogoBogoBoardItemDownloadLinkImpl.newInstance(id, value1, value2, value3, value4, fileId, fileName));
					}
				} catch (final NoSuchElementException e) {
					log.error(String.format("게시물에서 첨부파일에 대한 정보를 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", detailPageURL, elements.html()), e);
					throw e;
				}
			}
		} catch (final NoPermissionException e) {
			throw e;
		} catch (final NoSuchElementException e) {
			boardItem.clearDownloadLink();
			return false;
		} catch (final Exception e) {
			boardItem.clearDownloadLink();
			log.error("게시물({})의 첨부파일에 대한 정보를 로드하는 중에 예외가 발생하였습니다.", boardItem, e);
			return false;
		}
		
		return true;
	}

	@Override
	protected Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> downloadBoardItemDownloadLink0(final WebSiteBoardItem boardItem) {
		assert boardItem != null;
		assert isLogin() == true;

		int downloadTryCount = 0;
		int downloadCompletedCount = 0;
		Gson gson = new GsonBuilder().create();
		String detailPageURL = boardItem.getDetailPageURL();

		assert StringUtil.isBlank(detailPageURL) == false;
		
		Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			BogoBogoBoardItemDownloadLink downloadLink = (BogoBogoBoardItemDownloadLink) iterator.next();
			if (downloadLink.isDownloadable() == false || downloadLink.isDownloadCompleted() == true)
				continue;

			++downloadTryCount;

			log.info("검색된 게시물('{}')의 첨부파일을 다운로드합니다.({})", boardItem.getTitle(), downloadLink);

			try {
				/*
				  다운로드 페이지로 이동
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
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .ignoreContentType(true)
		                .execute();

				if (downloadProcess1Response.statusCode() != HttpStatus.SC_OK)
					throw new IOException("POST " + DOWNLOAD_PROCESS_URL_1 + " returned " + downloadProcess1Response.statusCode() + ": " + downloadProcess1Response.statusMessage());

				String result = downloadProcess1Response.parse().body().html();
				DownloadProcess1Result downloadProcess1Result = gson.fromJson(result, DownloadProcess1Result.class);
				if (StringUtil.isBlank(downloadProcess1Result.getStat()) == true || StringUtil.isBlank(downloadProcess1Result.getKey()) == true ||
						downloadProcess1Result.getStat().equals("true") == false) {
					throw new JsonParseException(String.format("첨부파일을 다운로드 하기 위한 작업 진행중에 수신된 데이터의 값이 유효하지 않습니다.(%s)", result));
				}

				/*
				  다운로드 링크 페이지 열기
				 */
				String downloadProcessURL2 = String.format("%s%s", DOWNLOAD_PROCESS_URL_2, downloadProcess1Result.getKey());
				Connection.Response downloadProcess2Response = Jsoup.connect(downloadProcessURL2)
						.userAgent(USER_AGENT)
						.data("dddd", downloadLink.getValue1())
		                .data("vvvv", downloadLink.getValue2())
		                .data("ssss", downloadLink.getValue3())
		                .data("code", downloadProcess1Result.getKey())
		                .data("file_id", downloadLink.getFileId())
		                .data("valid_id", downloadProcess1Result.getMsg())
		                .method(Connection.Method.POST)
		                .cookies(this.loginConnResponse.cookies())
						.timeout(URL_CONNECTION_TIMEOUT_LONG_MILLISECOND)
		                .execute();

				if (downloadProcess2Response.statusCode() != HttpStatus.SC_OK)
					throw new IOException("POST " + downloadProcessURL2 + " returned " + downloadProcess2Response.statusCode() + ": " + downloadProcess2Response.statusMessage());

				Document downloadProcess2Doc = downloadProcess2Response.parse();

				// 다운로드 받는 파일의 이름을 구한다.
				Elements elements = downloadProcess2Doc.select("#fileDetail p");
				if (elements.size() != 3)
					throw new ParseException(String.format("<P> 태그의 갯수 불일치로 파일명 추출이 실패하였습니다.(추출된갯수:%d) CSS셀렉터를 확인하세요.", elements.size()), 0);

				String fileName = elements.get(1).text().trim();
				if (fileName.startsWith("Filename:") == false)
					throw new ParseException(String.format("추출된 문자열이 특정 문자열로 시작되지 않아 파일명 추출이 실패하였습니다.(추출된문자열:%s) CSS셀렉터를 확인하세요.", fileName), 0);

				String downloadFilePath = String.format("%s%s", this.downloadFileWriteLocation, fileName.replace("Filename:", "").trim());
				File downloadFile = new File(downloadFilePath);
				if (downloadFile.exists() == true) {
					log.error("동일한 이름을 가진 파일이 이미 존재합니다. 해당 파일의 다운로드는 중지됩니다.({})", downloadFilePath);
					continue;
				}
				
				File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION);

				// 실제 다운로드 사이트에서 10초 대기후에 다운로드 받을 수 있으므로, 프로그램에서도 10초를 대기한다.
				// 이렇게 하지 않으면 첨부파일 다운로드가 실패하는 경우가 종종 발생한다.
				Thread.sleep(10000);

				/*
				  첨부파일 다운로드 하기
				 */
				Connection.Response downloadProcess3Response = Jsoup.connect(DOWNLOAD_PROCESS_URL_3)
						.userAgent(USER_AGENT)
						.header("Referer", downloadProcessURL2)
		                .data("dddd", downloadProcess2Doc.select("input[id=dddd]").val())
		                .data("vvvv", downloadProcess2Doc.select("input[id=vvvv]").val())
		                .data("file_id", downloadProcess2Doc.select("input[id=file_id]").val())
		                .data("valid_id", downloadProcess2Doc.select("input[id=valid_id]").val())
		                .method(Connection.Method.POST)
		                .cookies(downloadProcess2Response.cookies())
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .ignoreContentType(true)
		                .execute();

				if (downloadProcess3Response.statusCode() != HttpStatus.SC_OK)
					throw new IOException("POST " + DOWNLOAD_PROCESS_URL_3 + " returned " + downloadProcess3Response.statusCode() + ": " + downloadProcess3Response.statusMessage());

				if (downloadProcess3Response.parse().text().contains("Unauthorized Access") == true)
					throw new ParseException("첨부파일 다운로드 결과로 Unauthorized Access가 반환되었습니다.", 0);

				/*
				  첨부파일 저장
				 */
				FileOutputStream fos = new FileOutputStream(notyetDownloadFile);
				fos.write(downloadProcess3Response.bodyAsBytes());
				fos.close();

                notyetDownloadFile.renameTo(downloadFile);
				
				++downloadCompletedCount;
				downloadLink.setDownloadCompleted(true);

				log.info("검색된 게시물('{}')의 첨부파일 다운로드가 완료되었습니다.({})", boardItem.getTitle(), downloadFilePath);
			} catch (final Exception e) {
				log.error(String.format("첨부파일 다운로드 중에 예외가 발생하였습니다.(%s, %s)", boardItem, downloadLink), e);
			}
		}

		return new Tuple<>(downloadTryCount, downloadCompletedCount);
	}

	@Override
	public String toString() {
		return BogoBogo.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
