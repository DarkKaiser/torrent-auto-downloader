package com.darkkaiser.torrentad.website.impl.totoria;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.website.*;
import com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItemDownloadLink;
import com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItemDownloadLinkImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Totoria extends AbstractWebSite {

	private static final Logger logger = LoggerFactory.getLogger(Totoria.class);

	public static final String BASE_URL = "http://totoria.co";
	public static final String BASE_URL_WITH_DEFAULT_PATH = String.format("%s/bbs", BASE_URL);

	private static final String LOGIN_PROCESS_URL_1 = String.format("%s/login_check.php", BASE_URL_WITH_DEFAULT_PATH);

    // @@@@@
	private static final String DOWNLOAD_PROCESS_URL_1 = String.format("%s/download.php", BASE_URL_WITH_DEFAULT_PATH);
	private static final String DOWNLOAD_PROCESS_URL_2 = "http://linktender.net/";
	private static final String DOWNLOAD_PROCESS_URL_3 = "http://linktender.net/execDownload.php";

	private Connection.Response loginConnResponse;

	// 조회된 결과 목록
	private Map<TotoriaBoard, List<TotoriaBoardItem>> boardList = new HashMap<>();

	// 검색된 결과 목록
	private List<TotoriaSearchResultData> searchResultDataList = new LinkedList<>();

	// @@@@@
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

	public Totoria(final String owner, final String downloadFileWriteLocation) {
		this(null, owner, downloadFileWriteLocation);
	}

	public Totoria(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.TOTORIA, downloadFileWriteLocation);
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
				.data("url", "/")
				.data("mb_id", account.id())
                .data("mb_password", account.password())
				.method(Connection.Method.POST)
				.timeout(URL_CONNECTION_TIMEOUT_LONG_MILLISECOND)
				.execute();

		if (response.statusCode() != HttpStatus.SC_OK)
			throw new IOException("POST " + LOGIN_PROCESS_URL_1 + " returned " + response.statusCode() + ": " + response.statusMessage());

		// 로그인이 정상적으로 완료되었는지 확인한다.
		Document doc = response.parse();
		String outerHtml = doc.outerHtml();
		if (outerHtml.contains("<a href=\"http://totoria.co/bbs/logout.php\">로그아웃</a>") == false)		// '<a href="http://totoria.co/bbs/logout.php">로그아웃</a>' 문자열이 포함되어있는지 확인
			throw new IncorrectLoginAccountException("POST " + LOGIN_PROCESS_URL_1 + " return message:\n" + outerHtml);

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
	public Iterator<WebSiteBoardItem> list(final WebSiteBoard board, final boolean loadNow, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(comparator, "comparator");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		if (loadBoardItems0((TotoriaBoard) board, "", loadNow) == false)
			throw new LoadBoardItemsException(String.format("게시판 : %s", board.toString()));

		List<WebSiteBoardItem> resultList = new ArrayList<>();

		for (final TotoriaBoardItem boardItem : this.boardList.get(board)) {
			assert boardItem != null;

			resultList.add(boardItem);

			// logger.debug("조회된 게시물:" + boardItem);
		}

		resultList.sort(comparator);

		return resultList.iterator();
	}

	@Override
	public Iterator<WebSiteBoardItem> listAndFilter(final WebSiteSearchContext searchContext, final boolean loadNow, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
        Objects.requireNonNull(searchContext, "searchContext");
        Objects.requireNonNull(comparator, "comparator");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

        TotoriaSearchContext siteSearchContext = (TotoriaSearchContext) searchContext;

		if (loadBoardItems0(siteSearchContext.getBoard(), "", loadNow) == false)
			throw new LoadBoardItemsException(String.format("게시판 : %s", siteSearchContext.getBoard().toString()));

		List<WebSiteBoardItem> resultList = new ArrayList<>();

		long latestDownloadBoardItemIdentifier = siteSearchContext.getLatestDownloadBoardItemIdentifier();

		for (final TotoriaBoardItem boardItem : this.boardList.get(siteSearchContext.getBoard())) {
			assert boardItem != null;

			// 최근에 다운로드 한 게시물 이전의 게시물이라면 검색 대상에 포함시키지 않는다.
			if (latestDownloadBoardItemIdentifier != WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE && latestDownloadBoardItemIdentifier >= boardItem.getIdentifier())
				continue;

			if (siteSearchContext.isSatisfySearchCondition(WebSiteSearchKeywordsType.TITLE, boardItem.getTitle()) == true) {
				resultList.add(boardItem);

				// logger.debug("필터링된 게시물:" + boardItem);
			}
		}

		resultList.sort(comparator);

		return resultList.iterator();
	}

	// @@@@@
	@Override
	public Tuple<String/* 검색기록 Identifier */, Iterator<WebSiteBoardItem>/* 검색결과목록 */> search(final WebSiteBoard board, final String keyword, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException{
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(comparator, "comparator");

		if (StringUtil.isBlank(keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		// 이전에 동일한 검색 기록이 존재하는 경우, 이전 기록을 모두 제거한다.
		this.searchResultDataList.removeIf(searchResultData -> searchResultData.getBoard().equals(board) == true && searchResultData.getKeyword().equals(keyword) == true);

		// 오래된 검색 기록은 모두 제거한다.
		while (this.searchResultDataList.size() > (MAX_SEARCH_RESULT_DATA_COUNT - 1))
			this.searchResultDataList.remove(0);

		// 입력된 검색어를 이용하여 해당 게시판을 검색한다.
		List<TotoriaBoardItem> boardItems = loadBoardItems0_0((TotoriaBoard) board, String.format("&search=subject&keyword=%s&recom=", keyword));
		if (boardItems == null)
			throw new LoadBoardItemsException(String.format("게시판 : %s", board.toString()));

		List<WebSiteBoardItem> resultList = new ArrayList<>();

		for (final TotoriaBoardItem boardItem : boardItems) {
			assert boardItem != null;

			resultList.add(boardItem);

			// logger.debug("조회된 게시물:" + boardItem);
		}

		// 검색 기록을 남기고, 검색 결과 데이터를 반환한다.
        TotoriaSearchResultData searchResultData = new TotoriaSearchResultData(board, keyword, resultList);
		this.searchResultDataList.add(searchResultData);

		return new Tuple<>(searchResultData.getIdentifier(), searchResultData.resultIterator(comparator));
	}
	
	@Override
	public WebSiteSearchResultData getSearchResultData(final String identifier) {
		if (StringUtil.isBlank(identifier) == false) {
			for (final WebSiteSearchResultData searchResultData : this.searchResultDataList) {
				if (searchResultData.getIdentifier().equals(identifier) == true) {
					return searchResultData;
				}
			}
		}

		return null;
	}

    private boolean loadBoardItems0(final TotoriaBoard board, final String queryString, final boolean loadNow) throws NoPermissionException {
		assert board != null;
		assert isLogin() == true;

		if (loadNow == true) {
			this.boardList.remove(board);
		} else {
			if (this.boardList.containsKey(board) == true)
				return true;
		}

		List<TotoriaBoardItem> boardItems = loadBoardItems0_0(board, queryString);
		if (boardItems == null)
			return false;

		this.boardList.put(board, boardItems);

		return true;
	}

	private List<TotoriaBoardItem> loadBoardItems0_0(final TotoriaBoard board, final String queryString) throws NoPermissionException {
		assert board != null;
		assert isLogin() == true;

		String _queryString = queryString;
		if (StringUtil.isBlank(_queryString) == true)
			_queryString = "";
		if (_queryString.startsWith("&") == true)
			_queryString = _queryString.substring(1);

		String url = null;
		List<TotoriaBoardItem> boardItems = new ArrayList<>();

        // @@@@@ 검토
		try {
			for (int page = 1; page <= board.getDefaultLoadPageCount(); ++page) {
				url = String.format("%s&page=%d&%s", board.getURL(), page, _queryString);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .cookies(this.loginConnResponse.cookies())
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				Document boardItemsDoc = boardItemsResponse.parse();
				Elements elements = boardItemsDoc.select("div.list-webzine div.media-body");

				if (elements.isEmpty() == true) {
                    //noinspection StatementWithEmptyBody
                    if (boardItemsDoc.html().contains("게시물이 없습니다.") == true) {
						// 해당 페이지가 존재하지 않는 경우... 아무 처리도 하지 않는다.
					} else {
						throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
					}
				} else {
					try {
						for (final Element element : elements) {
							final Elements childrenElement = element.children();
                            final Iterator<Element> iterator = childrenElement.iterator();

                            //
                            // 제목
                            //
                            final Element titleElement = iterator.next();
                            final String title = trimString(titleElement.text());

                            Elements titleLinkElement = titleElement.getElementsByTag("a");
                            if (titleLinkElement.size() != 1)
                                throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 1개가 아닙니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

                            String detailPageURL = titleLinkElement.attr("href");
                            if (detailPageURL.startsWith(String.format("%s/board.php", Totoria.BASE_URL_WITH_DEFAULT_PATH)) == false)
                                throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

                            int noPos = detailPageURL.indexOf("wr_id=");
                            if (noPos < 0)
                                throw new ParseException(String.format("게시물의 ID 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
                            String identifier = detailPageURL.substring(noPos + 6/* wr_id= */, detailPageURL.indexOf("&", noPos));

                            //
                            // 날짜
                            //
                            final Element registDateElement = iterator.next();
                            final Iterator<Element> descriptionIterator = registDateElement.children().iterator();
                            descriptionIterator.next();     // 댓글
                            descriptionIterator.next();     // 조회
                            descriptionIterator.next();     // 추천

                            String registDate = descriptionIterator.next().text().trim();
                            if (registDate.startsWith("|") == true)
                                registDate = registDate.substring(1).trim();

                            if (registDate.lastIndexOf("분전") != -1) {
                                int minute = Integer.parseInt(registDate.substring(0, registDate.length() - 2));

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.MINUTE, -1 * minute);

                                registDate = (new SimpleDateFormat(board.getDefaultRegistDateFormatString())).format(cal.getTime());
                            } else if (registDate.lastIndexOf("시간전") != -1) {
                                int hour = Integer.parseInt(registDate.substring(0, registDate.length() - 3));

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.HOUR, -1 * hour);

                                registDate = (new SimpleDateFormat(board.getDefaultRegistDateFormatString())).format(cal.getTime());
                            } else if (registDate.lastIndexOf("일전") != -1) {
                                int date = Integer.parseInt(registDate.substring(0, registDate.length() - 2));

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.DATE, -1 * date);

                                registDate = (new SimpleDateFormat(board.getDefaultRegistDateFormatString())).format(cal.getTime());
                            }

							boardItems.add(new TotoriaBoardItem(board, Long.parseLong(identifier), title, registDate, detailPageURL));
						}
					} catch (final NoSuchElementException e) {
						logger.error(String.format("게시물을 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, elements.html()), e);
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
			logger.error(String.format("게시판(%s) 데이터를 로드하는 중에 예외가 발생하였습니다.(URL:%s)", board, url), e);
			return null;
		}

		boardItems.sort(new WebSiteBoardItemComparatorIdentifierAsc());

		return boardItems;
	}

	@Override
	public boolean loadDownloadLink(final WebSiteBoardItem boardItem) throws NoPermissionException {
        Objects.requireNonNull(boardItem, "boardItem");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		TotoriaBoardItem siteBoardItem = (TotoriaBoardItem) boardItem;

		// 첨부파일에 대한 다운로드 링크를 읽어들인다. 
		Iterator<WebSiteBoardItemDownloadLink> iterator = siteBoardItem.downloadLinkIterator();
		if (iterator.hasNext() == false) {
			if (loadBoardItemDownloadLink0(siteBoardItem) == false) {
				logger.error(String.format("첨부파일에 대한 정보를 읽어들일 수 없습니다.(%s)", boardItem));
				return false;
			}
		}

		assert siteBoardItem.downloadLinkIterator().hasNext() == true;

		return true;
	}

	// @@@@@
	@Override
	public Tuple<Integer, Integer> download(final WebSiteBoardItem boardItem, final WebSiteSearchContext searchContext) throws NoPermissionException {
        Objects.requireNonNull(searchContext, "searchContext");
        Objects.requireNonNull(boardItem, "boardItem");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

        TotoriaBoardItem siteBoardItem = (TotoriaBoardItem) boardItem;
        TotoriaSearchContext siteSearchContext = (TotoriaSearchContext) searchContext;

		// 첨부파일에 대한 다운로드 링크를 읽어들인다. 
		Iterator<WebSiteBoardItemDownloadLink> iterator = siteBoardItem.downloadLinkIterator();
		if (iterator.hasNext() == false) {
			if (loadBoardItemDownloadLink0(siteBoardItem) == false) {
				logger.error(String.format("첨부파일에 대한 정보를 읽어들일 수 없어, 첨부파일 다운로드가 실패하였습니다.(%s)", boardItem));
				return new Tuple<>(-1, -1);
			}
		}

		assert siteBoardItem.downloadLinkIterator().hasNext() == true;

		// 다운로드 링크에서 다운로드 제외 대상은 제외시킨다.
		iterator = siteBoardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			BogoBogoBoardItemDownloadLink downloadLink = (BogoBogoBoardItemDownloadLink) iterator.next();
			downloadLink.setDownloadable(siteSearchContext.isSatisfySearchCondition(WebSiteSearchKeywordsType.FILE, downloadLink.getFileName()));
		}

		return downloadBoardItemDownloadLink0(siteBoardItem);
	}

	// @@@@@
	@Override
	public Tuple<Integer, Integer> download(final WebSiteBoardItem boardItem, final long downloadLinkIndex) throws NoPermissionException {
        Objects.requireNonNull(boardItem, "boardItem");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

        TotoriaBoardItem siteBoardItem = (TotoriaBoardItem) boardItem;

		// 첨부파일에 대한 다운로드 링크를 읽어들인다. 
		Iterator<WebSiteBoardItemDownloadLink> iterator = siteBoardItem.downloadLinkIterator();
		if (iterator.hasNext() == false) {
			if (loadBoardItemDownloadLink0(siteBoardItem) == false) {
				logger.error(String.format("첨부파일에 대한 정보를 읽어들일 수 없어, 첨부파일 다운로드가 실패하였습니다.(%s)", boardItem));
				return new Tuple<>(-1, -1);
			}
		}

		assert siteBoardItem.downloadLinkIterator().hasNext() == true;

		// 다운로드 링크에서 다운로드 제외 대상은 제외시킨다.
		iterator = siteBoardItem.downloadLinkIterator();
		for (int index = 0; iterator.hasNext() == true; ++index) {
			WebSiteBoardItemDownloadLink downloadLink = iterator.next();
			downloadLink.setDownloadable(index == downloadLinkIndex);
		}

		return downloadBoardItemDownloadLink0(siteBoardItem);
	}

	// @@@@@
	private boolean loadBoardItemDownloadLink0(final TotoriaBoardItem boardItem) throws NoPermissionException {
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
					logger.error(String.format("게시물에서 첨부파일에 대한 정보를 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", detailPageURL, elements.html()), e);
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
			logger.error("게시물({})의 첨부파일에 대한 정보를 로드하는 중에 예외가 발생하였습니다.", boardItem, e);
			return false;
		}
		
		return true;
	}

	// @@@@@
	private Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> downloadBoardItemDownloadLink0(final TotoriaBoardItem boardItem) {
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

			logger.info("검색된 게시물('{}')의 첨부파일을 다운로드합니다.({})", boardItem.getTitle(), downloadLink);

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
					logger.error("동일한 이름을 가진 파일이 이미 존재합니다. 해당 파일의 다운로드는 중지됩니다.({})", downloadFilePath);
					continue;
				}
				
				File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOADED_FILE_EXTENSION);

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
				
				logger.info("검색된 게시물('{}')의 첨부파일 다운로드가 완료되었습니다.({})", boardItem.getTitle(), downloadFilePath);
			} catch (final Exception e) {
				logger.error(String.format("첨부파일 다운로드 중에 예외가 발생하였습니다.(%s, %s)", boardItem, downloadLink), e);
			}
		}

		return new Tuple<>(downloadTryCount, downloadCompletedCount);
	}

	@Override
	public String toString() {
		return Totoria.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
