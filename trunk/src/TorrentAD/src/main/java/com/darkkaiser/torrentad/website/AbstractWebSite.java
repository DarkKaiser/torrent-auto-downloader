package com.darkkaiser.torrentad.website;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public abstract class AbstractWebSite implements WebSiteConnection, WebSiteHandler, WebSiteContext {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSite.class);

	protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	protected static final int URL_CONNECTION_TIMEOUT_LONG_MILLISECOND = 60 * 1000;
	protected static final int URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND = 15 * 1000;

	protected static final int MAX_SEARCH_RESULT_DATA_COUNT = 100;

	protected final WebSiteConnector siteConnector;
	
	protected final String owner;

	protected final WebSite site;

	protected WebSiteAccount account;

	// 다운로드 받은 파일이 저장되는 위치
	protected final String downloadFileWriteLocation;

	// 조회된 결과 목록
	protected Map<WebSiteBoard, List<WebSiteBoardItem>> boardList = new HashMap<>();

	// 검색된 결과 목록
	protected List<DefaultWebSiteSearchResultData> searchResultDataList = new LinkedList<>();

	protected AbstractWebSite(final WebSiteConnector siteConnector, final String owner, final WebSite site, final String downloadFileWriteLocation) {
		if (StringUtil.isBlank(owner) == true)
			throw new IllegalArgumentException("owner는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(downloadFileWriteLocation) == true)
			throw new IllegalArgumentException("downloadFileWriteLocation은 빈 문자열을 허용하지 않습니다.");

		Objects.requireNonNull(site, "site");

		this.siteConnector = siteConnector;

		this.site = site;
		this.owner = owner;

		if (downloadFileWriteLocation.endsWith(File.separator) == true) {
			this.downloadFileWriteLocation = downloadFileWriteLocation;
		} else {
			this.downloadFileWriteLocation = String.format("%s%s", downloadFileWriteLocation, File.separator);
		}
	}
	
	@Override
	public void login(final WebSiteAccount account) throws Exception {
		logger.info("{} 에서 웹사이트('{}')를 로그인합니다.", getOwner(), getName());

		logout0();

		try {
			// 도메인 리다이렉션 여부를 확인한다.
			// 리다이렉션 되는 경우 토렌트 사이트 URL을 리다이렉션 되는 URL로 변경해준다.
			checkDomainRedirection();
		} catch (final IOException e) {
			logger.warn("도메인 리다이렉션 여부를 확인하는 중에 예외가 발생하였습니다.", e);
		}

		login0(account);

		logger.info("{} 에서 웹사이트('{}')가 로그인 되었습니다.", getOwner(), getName());
	}
	
	protected void login0(final WebSiteAccount account) throws Exception {

	}
	
	@Override
	public void logout() throws Exception {
		logger.info("{} 에서 웹사이트('{}')를 로그아웃합니다.", getOwner(), getName());

		logout0();

		logger.info("{} 에서 웹사이트('{}')가 로그아웃 되었습니다.", getOwner(), getName());
	}

	protected void logout0() throws Exception {

	}

	@Override
	public boolean isLogin() {
		return true;
	}

	private void checkDomainRedirection() throws IOException {
		Connection.Response response = Jsoup.connect(getBaseURL())
				.userAgent(USER_AGENT)
				.method(Connection.Method.GET)
				.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
				.execute();

		if (response.statusCode() == HttpStatus.SC_OK) {
			String baseURL = getBaseURL();
			if (baseURL.endsWith("/") == true)
				baseURL = baseURL.substring(0, baseURL.length() - 1);

			String responseURL = response.url().toString();
			final int pos = responseURL.indexOf("/", responseURL.indexOf("//") + 2);
			if (pos != -1)
				responseURL = responseURL.substring(0, pos);

			if (responseURL.endsWith("/") == true)
				responseURL = responseURL.substring(0, responseURL.length() - 1);

			if (baseURL.equals(responseURL) == false) {
				setBaseURL(responseURL);

				// 도메인 변경사항을 파일에 반영한다.
				final List<String> lines = Files.readAllLines(Paths.get(Constants.APP_CONFIG_FILE_NAME), StandardCharsets.UTF_8);

				boolean find = false;
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).contains(Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL) == true) {
						find = true;
						lines.set(i, "\t\t\t<" + Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL + ">" + responseURL + "</" + Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL + ">");
						break;
					}
				}

				if (find == true)
					Files.write(Paths.get(Constants.APP_CONFIG_FILE_NAME), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			}
		}
	}

	public WebSiteConnector getSiteConnector() {
		return this.siteConnector;
	}

	protected String getOwner() {
		return this.owner;
	}

	@Override
	public String getName() {
		return this.site.getName();
	}

	@Override
	public String getBaseURL() {
		return this.site.getBaseURL();
	}

	protected void setBaseURL(final String url) {
		this.site.setBaseURL(url);
	}

	@Override
	public WebSiteAccount getAccount() {
		return this.account;
	}

	@Override
	public void setAccount(final WebSiteAccount account) {
		this.account = account;
	}

	@Override
	public Iterator<WebSiteBoardItem> list(final WebSiteBoard board, final boolean loadNow, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
		Objects.requireNonNull(board, "board");
		Objects.requireNonNull(comparator, "comparator");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		if (loadBoardItems0(board, "", loadNow) == false)
			throw new LoadBoardItemsException(String.format("게시판 : %s", board.toString()));

		final List<WebSiteBoardItem> resultList = new ArrayList<>();

		for (final WebSiteBoardItem boardItem : this.boardList.get(board)) {
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

		if (loadBoardItems0(searchContext.getBoard(), "", loadNow) == false)
			throw new LoadBoardItemsException(String.format("게시판 : %s", searchContext.getBoard().toString()));

		final List<WebSiteBoardItem> resultList = new ArrayList<>();

		long latestDownloadBoardItemIdentifier = searchContext.getLatestDownloadBoardItemIdentifier();

		for (final WebSiteBoardItem boardItem : this.boardList.get(searchContext.getBoard())) {
			assert boardItem != null;

			// 최근에 다운로드 한 게시물 이전의 게시물이라면 검색 대상에 포함시키지 않는다.
			if (latestDownloadBoardItemIdentifier != WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE && latestDownloadBoardItemIdentifier >= boardItem.getIdentifier())
				continue;

			if (searchContext.isSatisfySearchCondition(WebSiteSearchKeywordsType.TITLE, boardItem.getTitle()) == true) {
				resultList.add(boardItem);

				// logger.debug("필터링된 게시물:" + boardItem);
			}
		}

		resultList.sort(comparator);

		return resultList.iterator();
	}

	@Override
	public Tuple<String/* 검색기록 Identifier */, Iterator<WebSiteBoardItem>/* 검색결과목록 */> search(final WebSiteBoard board, final String keyword, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
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
		List<WebSiteBoardItem> boardItems = loadBoardItems0_0(board, getSearchQueryString(keyword));
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

	protected abstract String getSearchQueryString(final String keyword);

	private boolean loadBoardItems0(final WebSiteBoard board, final String queryString, final boolean loadNow) throws NoPermissionException {
		assert board != null;
		assert isLogin() == true;

		if (loadNow == true) {
			this.boardList.remove(board);
		} else {
			if (this.boardList.containsKey(board) == true)
				return true;
		}

		final List<WebSiteBoardItem> boardItems = loadBoardItems0_0(board, queryString);
		if (boardItems == null)
			return false;

		this.boardList.put(board, boardItems);

		return true;
	}

	protected abstract List<WebSiteBoardItem> loadBoardItems0_0(final WebSiteBoard board, final String queryString) throws NoPermissionException;

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

	@Override
	public boolean loadDownloadLink(final WebSiteBoardItem boardItem) throws NoPermissionException {
		Objects.requireNonNull(boardItem, "boardItem");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		// 첨부파일에 대한 다운로드 링크를 읽어들인다.
		final Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		if (iterator.hasNext() == false) {
			if (loadBoardItemDownloadLink0(boardItem) == false) {
				logger.error(String.format("첨부파일에 대한 정보를 읽어들일 수 없습니다.(%s)", boardItem));
				return false;
			}
		}

		assert boardItem.downloadLinkIterator().hasNext() == true;

		return true;
	}

	@Override
	public Tuple<Integer, Integer> download(final WebSiteBoardItem boardItem, final WebSiteSearchContext searchContext) throws NoPermissionException {
		Objects.requireNonNull(searchContext, "searchContext");
		Objects.requireNonNull(boardItem, "boardItem");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		// 첨부파일에 대한 다운로드 링크를 읽어들인다.
		Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		if (iterator.hasNext() == false) {
			if (loadBoardItemDownloadLink0(boardItem) == false) {
				logger.error(String.format("첨부파일에 대한 정보를 읽어들일 수 없어, 첨부파일 다운로드가 실패하였습니다.(%s)", boardItem));
				return new Tuple<>(-1, -1);
			}
		}

		assert boardItem.downloadLinkIterator().hasNext() == true;

		// 다운로드 링크에서 다운로드 제외 대상은 제외시킨다.
		iterator = boardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			WebSiteBoardItemDownloadLink downloadLink = iterator.next();
			downloadLink.setDownloadable(searchContext.isSatisfySearchCondition(WebSiteSearchKeywordsType.FILE, downloadLink.getFileName()));
		}

		return downloadBoardItemDownloadLink0(boardItem);
	}

	@Override
	public Tuple<Integer, Integer> download(final WebSiteBoardItem boardItem, final long downloadLinkIndex) throws NoPermissionException {
		Objects.requireNonNull(boardItem, "boardItem");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		// 첨부파일에 대한 다운로드 링크를 읽어들인다.
		Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		if (iterator.hasNext() == false) {
			if (loadBoardItemDownloadLink0(boardItem) == false) {
				logger.error(String.format("첨부파일에 대한 정보를 읽어들일 수 없어, 첨부파일 다운로드가 실패하였습니다.(%s)", boardItem));
				return new Tuple<>(-1, -1);
			}
		}

		assert boardItem.downloadLinkIterator().hasNext() == true;

		// 다운로드 링크에서 다운로드 제외 대상은 제외시킨다.
		iterator = boardItem.downloadLinkIterator();
		for (int index = 0; iterator.hasNext() == true; ++index) {
			WebSiteBoardItemDownloadLink downloadLink = iterator.next();
			downloadLink.setDownloadable(index == downloadLinkIndex);
		}

		return downloadBoardItemDownloadLink0(boardItem);
	}

	protected abstract boolean loadBoardItemDownloadLink0(final WebSiteBoardItem boardItem) throws NoPermissionException;

	protected abstract Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> downloadBoardItemDownloadLink0(final WebSiteBoardItem boardItem);

	protected String trimString(final String value) {
		int start = 0;
		int length = value.length();

		// Character.isSpaceChar() : trim()으로 제거되지 않는 공백(no-break space=줄바꿈없는공백)을 제거하기 위해 사용한다.

		while ((start < length) && (Character.isSpaceChar(value.charAt(start)) == true)) {
			start++;
		}
		while ((start < length) && (Character.isSpaceChar(value.charAt(length - 1)) == true)) {
			length--;
		}

		return ((start > 0) || (length < value.length())) ? value.substring(start, length) : value;
	}

	@Override
	public String toString() {
		return AbstractWebSite.class.getSimpleName() +
				"{" +
				"owner:" + getOwner() +
				", site:" + this.site +
				", account:" + getAccount() +
				", downloadFileWriteLocation:" + this.downloadFileWriteLocation +
				"}";
	}

}
