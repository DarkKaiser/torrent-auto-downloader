package com.darkkaiser.torrentad.website.impl.torrentmi;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.website.*;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TorrentMi extends AbstractWebSite {

	private static final Logger logger = LoggerFactory.getLogger(TorrentMi.class);

	public static final String BASE_URL = "https://www.torrentmi2.com";
	public static final String BASE_URL_WITH_DEFAULT_PATH = String.format("%s", BASE_URL);

	private static final String FILETENDER_DOWNLOAD_URL = "http://file.filetender.com/file2.php";

	public TorrentMi(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.TORRENTMI, downloadFileWriteLocation);
	}

	@Override
	protected void login0(final WebSiteAccount account) throws Exception {
		// 비회원제로 운영되기 때문에 아무 처리도 하지 않는다.
	}

	@Override
	protected void logout0() throws Exception {
		// 비회원제로 운영되기 때문에 아무 처리도 하지 않는다.
	}

	@Override
	public boolean isLogin() {
		// 비회원제로 운영되기 때문에 무조건 true를 반환한다.
		return true;
	}

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
		List<WebSiteBoardItem> boardItems;
		try {
			boardItems = loadBoardItems0_0(board, String.format("&sc=%s", URLEncoder.encode(keyword, "UTF-8")));
		} catch (final UnsupportedEncodingException e) {
			throw new LoadBoardItemsException(String.format("게시판 : %s", board.toString()));
		}
		if (boardItems == null)
			throw new LoadBoardItemsException(String.format("게시판 : %s", board.toString()));

		List<WebSiteBoardItem> resultList = new ArrayList<>();

		for (final WebSiteBoardItem boardItem : boardItems) {
			assert boardItem != null;

			resultList.add(boardItem);

			// logger.debug("조회된 게시물:" + boardItem);
		}

		// 검색 기록을 남기고, 검색 결과 데이터를 반환한다.
		DefaultWebSiteSearchResultData searchResultData = new DefaultWebSiteSearchResultData(board, keyword, resultList);
		this.searchResultDataList.add(searchResultData);

		return new Tuple<>(searchResultData.getIdentifier(), searchResultData.resultIterator(comparator));
	}

	@SuppressWarnings("Duplicates")
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
			for (int page = 1; page <= board.getDefaultLoadPageCount(); ++page) {
				url = String.format("%s&page=%d&%s", board.getURL(), page, _queryString);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				Document boardItemsDoc = boardItemsResponse.parse();
				Elements elements = boardItemsDoc.select("div.sub_list > div > table > tbody > tr");

				if (elements.isEmpty() == true) {
					// 조회된 게시물이 0건인 경우인지 확인한다.
					Elements elements2 = boardItemsDoc.select("div.sub_list > div > table > tbody");
					if (elements2.isEmpty() == false)
						continue;

					throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
				} else {
					try {
						for (final Element element : elements) {
							final Iterator<Element> iterator = element.getElementsByTag("td").iterator();

							//
							// 번호
							//
							iterator.next().text();

							//
							// 제목
							//
							final Element titleElement = iterator.next();

							final Elements titleLinkElement = titleElement.getElementsByTag("a");
							if (titleLinkElement.size() == 0 || titleLinkElement.size() > 2)
								throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

							String detailPageURL = titleLinkElement.get(titleLinkElement.size() - 1).attr("href");
							if (detailPageURL.startsWith("view.php") == false)
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

							int noPosBegin = detailPageURL.indexOf("&id=");
							if (noPosBegin < 0)
								throw new ParseException(String.format("게시물의 ID 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							int noPosEnd = detailPageURL.indexOf("&", noPosBegin + 1);
							if (noPosEnd == -1)
								noPosEnd = detailPageURL.length();

							final String title = trimString(titleLinkElement.get(titleLinkElement.size() - 1).text());
							final String identifier = detailPageURL.substring(noPosBegin + 4/* &id= */, noPosEnd);

							//
							// 날짜
							//
							String registDate = iterator.next().text().trim();

							if (registDate.matches("^[0-9]{2}:[0-9]{2}$"/* 게시물이 등록된 오늘 시간 */) == true) {
								final String[] timeSplit = registDate.split(":");

								final Calendar cal = Calendar.getInstance();
								cal.setTime(new Date());
								cal.set(Calendar.HOUR, Integer.parseInt(timeSplit[0]));
								cal.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));

								registDate = (new SimpleDateFormat(board.getDefaultRegistDateFormatString())).format(cal.getTime());
							} else if (registDate.matches("^[0-9]{2}-[0-9]{2}$"/* 게시물이 등록된 일자 */) == true) {
								String[] daySplit = registDate.split("-");

								final Calendar cal = Calendar.getInstance();
								cal.setTime(new Date());

								final int thisMonth = cal.get(Calendar.MONTH);
								final int thisDate = cal.get(Calendar.DATE);

								final int postingMonth = Integer.parseInt(daySplit[0]) - 1;
								final int postingDate = Integer.parseInt(daySplit[1]);

								if (thisMonth < postingMonth || (thisMonth == postingMonth && thisDate < postingDate))
									cal.add(Calendar.YEAR, -1);

								cal.set(Calendar.MONTH, postingMonth);
								cal.set(Calendar.DATE, postingDate);

								registDate = (new SimpleDateFormat(board.getDefaultRegistDateFormatString())).format(cal.getTime());
							} else {
								throw new ParseException(String.format("게시물의 날짜 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}

							boardItems.add(new DefaultWebSiteBoardItem(board, Long.parseLong(identifier), title, registDate, String.format("%s/%s", TorrentMi.BASE_URL_WITH_DEFAULT_PATH, detailPageURL)));
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
	protected boolean loadBoardItemDownloadLink0(final WebSiteBoardItem boardItem) throws NoPermissionException {
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
	                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
	                .execute();

			if (detailPageResponse.statusCode() != HttpStatus.SC_OK)
				throw new IOException("GET " + detailPageURL + " returned " + detailPageResponse.statusCode() + ": " + detailPageResponse.statusMessage());

			Document detailPageDoc = detailPageResponse.parse();
			Elements elements = detailPageDoc.select(".downLoad > a:first-child");

			if (elements.isEmpty() == true) {
				throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
			} else {
				try {
					String[] exceptFileExtension = { "JPG", "JPEG", "GIF", "PNG" };

					for (final Element element : elements) {
						String link = element.attr("href");
						String fileName = trimString(element.text());

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtension).contains(fileName.toUpperCase()) == true)
							continue;

						boardItem.addDownloadLink(TorrentMiBoardItemDownloadLinkImpl.newInstance(link, fileName));
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

	@Override
	protected Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> downloadBoardItemDownloadLink0(final WebSiteBoardItem boardItem) {
		assert boardItem != null;
		assert isLogin() == true;

		int downloadTryCount = 0;
		int downloadCompletedCount = 0;
		String detailPageURL = boardItem.getDetailPageURL();

		assert StringUtil.isBlank(detailPageURL) == false;

		Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			TorrentMiBoardItemDownloadLink downloadLink = (TorrentMiBoardItemDownloadLink) iterator.next();
			if (downloadLink.isDownloadable() == false || downloadLink.isDownloadCompleted() == true)
				continue;

			++downloadTryCount;

			logger.info("검색된 게시물('{}')의 첨부파일을 다운로드합니다.({})", boardItem.getTitle(), downloadLink);

			try {
				// 다운로드 받는 파일의 이름을 구한다.
				final String downloadFilePath = String.format("%s%s", this.downloadFileWriteLocation, downloadLink.getFileName().trim());
				final File downloadFile = new File(downloadFilePath);
				if (downloadFile.exists() == true) {
					logger.error("동일한 이름을 가진 파일이 이미 존재합니다. 해당 파일의 다운로드는 중지됩니다.({})", downloadFilePath);
					continue;
				}

				/*
				  FileTender 다운로드 페이지 열기
				 */
				final String downloadLinkPage = downloadLink.getLink();
				Connection.Response downloadLinkPageResponse = Jsoup.connect(downloadLinkPage)
						.userAgent(USER_AGENT)
						.header("Referer", detailPageURL)
						.method(Connection.Method.POST)
						.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
						.ignoreContentType(true)
						.execute();

				if (downloadLinkPageResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("POST " + downloadLinkPage + " returned " + downloadLinkPageResponse.statusCode() + ": " + downloadLinkPageResponse.statusMessage());

				Document downloadLinkPageDoc = downloadLinkPageResponse.parse();

				String key = downloadLinkPageDoc.select("input[name=key]").val();
				String ticket = downloadLinkPageDoc.select("input[name=Ticket]").val();
				String randStr = downloadLinkPageDoc.select("input[name=Randstr]").val();
				String userIP = downloadLinkPageDoc.select("input[name=UserIP]").val();

				if (key.equals("") == true || userIP.equals("") == true)
					throw new ParseException(String.format("첨부파일을 다운로드 하기 위한 작업 진행중에 수신된 데이터의 값이 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s, key:%s, Ticket:%s, Randstr:%s, UserIP:%s)", downloadLinkPage, key, ticket, randStr, userIP), 0);

				File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOADED_FILE_EXTENSION);

				/*
				  첨부파일 다운로드 하기
				 */
				Connection.Response downloadProcessResponse = Jsoup.connect(FILETENDER_DOWNLOAD_URL)
						.userAgent(USER_AGENT)
						.data("key", key)
						.data("Ticket", ticket)
						.data("Randstr", randStr)
						.data("UserIP", userIP)
						.method(Connection.Method.GET)
						.cookies(downloadLinkPageResponse.cookies())
						.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
						.ignoreContentType(true)
						.execute();

				if (downloadProcessResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("POST " + FILETENDER_DOWNLOAD_URL + " returned " + downloadProcessResponse.statusCode() + ": " + downloadProcessResponse.statusMessage());

				/*
				  첨부파일 저장
				 */
				FileOutputStream fos = new FileOutputStream(notyetDownloadFile);
				fos.write(downloadProcessResponse.bodyAsBytes());
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
		return TorrentMi.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
