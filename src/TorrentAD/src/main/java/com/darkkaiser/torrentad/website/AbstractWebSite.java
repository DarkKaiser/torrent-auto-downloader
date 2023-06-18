package com.darkkaiser.torrentad.website;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.util.notifyapi.NotifyApiClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public abstract class AbstractWebSite implements WebSiteConnection, WebSiteHandler, WebSiteContext {

	protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	protected static final int URL_CONNECTION_TIMEOUT_LONG_MILLISECOND = 60 * 1000;
	protected static final int URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND = 15 * 1000;

	protected static final int MAX_SEARCH_RESULT_DATA_COUNT = 100;

	@Getter
	protected final WebSiteConnector siteConnector;

	@Getter
	protected final String owner;

	protected final WebSite site;

	@Getter
	@Setter
	protected WebSiteAccount account;

	// 다운로드 받은 파일이 저장되는 위치
	protected final String downloadFileWriteLocation;

	// 조회된 결과 목록
	protected final Map<WebSiteBoard, List<WebSiteBoardItem>> boardList = new HashMap<>();

	// 검색된 결과 목록
	protected final List<DefaultWebSiteSearchResultData> searchResultDataList = new LinkedList<>();

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
		log.info("{} 에서 웹사이트('{}')를 로그인합니다.", getOwner(), getName());

		logout0();

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 인증서 전체 허용 코드 시작
		//  * SSL 통신 오류(Java PKIX path building failed) 해결 코드
		//    - 오류메시지 : PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested
		//    - 참고사이트 : https://velog.io/@csk917work/%EC%98%A4%ED%94%88-API-%EC%82%AC%EC%9A%A9%EC%8B%9C-SSL-PKIX-path-building
		final TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}
		};

		try {
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (final NoSuchAlgorithmException | KeyManagementException e) {
			final String message = "SSL 통신 오류에 대한 인증서 전체 허용 코드를 실행하는 중에 예외가 발생하였습니다.";

			log.warn(message, e);
			NotifyApiClient.sendNotifyMessage(message, true);
		}
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 인증서 전체 허용 코드 끝

		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 도메인 리다이렉션 여부 코드 시작
		try {
			// 리다이렉션 되는 경우 토렌트 사이트 URL을 리다이렉션 되는 URL로 변경해준다.
			checkDomainRedirection();
		} catch (final IOException e) {
			final String message = String.format("도메인(%s) 리다이렉션 여부를 확인하는 중에 예외가 발생하였습니다.", getBaseURL());

			log.warn(message, e);
			NotifyApiClient.sendNotifyMessage(message, true);

			throw e;
		}
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 도메인 리다이렉션 여부 코드 끝

		login0(account);

		log.info("{} 에서 웹사이트('{}')가 로그인 되었습니다.", getOwner(), getName());
	}
	
	protected void login0(final WebSiteAccount account) throws Exception {

	}
	
	@Override
	public void logout() throws Exception {
		log.info("{} 에서 웹사이트('{}')를 로그아웃합니다.", getOwner(), getName());

		logout0();

		log.info("{} 에서 웹사이트('{}')가 로그아웃 되었습니다.", getOwner(), getName());
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
				final Path configFilePath = Paths.get(Constants.APP_CONFIG_FILE_NAME);
				final List<String> lines = Files.readAllLines(configFilePath, StandardCharsets.UTF_8);

				boolean find = false;
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).contains(Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL) == true) {
						find = true;
						lines.set(i, "\t\t\t<" + Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL + ">" + responseURL + "</" + Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL + ">");
						break;
					}
				}

				if (find == true)
					Files.write(configFilePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			}
		}
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
	public Iterator<WebSiteBoardItem> list(final WebSiteBoard board, final boolean loadNow, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
		Objects.requireNonNull(board, "board");
		Objects.requireNonNull(comparator, "comparator");

		if (isLogin() == false)
			throw new IllegalStateException("로그인 상태가 아닙니다.");

		if (loadBoardItems0(board, "", loadNow) == false)
			throw new LoadBoardItemsException(String.format("게시판 : %s", board));

		final List<WebSiteBoardItem> resultList = new ArrayList<>();

		for (final WebSiteBoardItem boardItem : this.boardList.get(board)) {
			assert boardItem != null;

			resultList.add(boardItem);

			// log.debug("조회된 게시물:" + boardItem);
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

				// log.debug("필터링된 게시물:" + boardItem);
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
			throw new LoadBoardItemsException(String.format("게시판 : %s", board));

		List<WebSiteBoardItem> resultList = new ArrayList<>();

		for (final WebSiteBoardItem boardItem : boardItems) {
			assert boardItem != null;

			resultList.add(boardItem);

			// log.debug("조회된 게시물:" + boardItem);
		}

		// 검색 기록을 남기고, 검색 결과 데이터를 반환한다.
		DefaultWebSiteSearchResultData searchResultData = new DefaultWebSiteSearchResultData(board, keyword, resultList);
		this.searchResultDataList.add(searchResultData);

		return new Tuple<>(searchResultData.getIdentifier(), searchResultData.resultIterator(comparator));
	}

	protected abstract String getSearchQueryString(final String keyword);

	@SuppressWarnings("SameParameterValue")
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
				final String message = String.format("첨부파일에 대한 정보를 읽어들일 수 없습니다.(%s)", boardItem);

				log.error(message);
				NotifyApiClient.sendNotifyMessage(message, true);

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
				final String message = String.format("첨부파일에 대한 정보를 읽어들일 수 없어, 첨부파일 다운로드가 실패하였습니다.(%s)", boardItem);

				log.error(message);
				NotifyApiClient.sendNotifyMessage(message, true);

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
				final String message = String.format("첨부파일에 대한 정보를 읽어들일 수 없어, 첨부파일 다운로드가 실패하였습니다.(%s)", boardItem);

				log.error(message);
				NotifyApiClient.sendNotifyMessage(message, true);

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

	protected boolean isNotYetDownloadSubtitleZipFile(final String filePath) {
		if (StringUtil.isBlank(filePath) == true)
			return false;

		if (filePath.endsWith(Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION) == false)
			return false;

		return filePath.substring(0, filePath.length() - Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION.length()).toLowerCase().endsWith(".zip");
	}

	protected boolean extractNotYetDownloadSubtitleZipFile(final String filePath, final List<String> extractedSubtitleFilePathList) {
		if (isNotYetDownloadSubtitleZipFile(filePath) == false)
			return false;

		ZipEntry ze;
		boolean decompressible = true;
		final List<String> subtitleFileExtensions = Arrays.asList(".SMI", ".SRT");

		// 폴더가 포함되어 있거나, 자막 파일 이외의 파일이 존재하는지 확인한다.
		try (final FileInputStream fis = new FileInputStream(filePath);
			 final ZipInputStream zis = new ZipInputStream(fis)) {
			ze = zis.getNextEntry();
			while (ze != null) {
				final String fileName = ze.getName();
				if (ze.isDirectory() == true || subtitleFileExtensions.contains(fileName.substring(fileName.lastIndexOf(".")).toUpperCase()) == false) {
					decompressible = false;
					break;
				}

				zis.closeEntry();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
		} catch (final IOException e) {
			final String message = "압축된 자막 파일의 압축을 해제하는 중에 예외가 발생하였습니다.(1)";

			log.warn(message, e);
			NotifyApiClient.sendNotifyMessage(message, true);

			return false;
		}

		if (decompressible == false)
			return false;

		try (FileInputStream fis = new FileInputStream(filePath);
			 ZipInputStream zis = new ZipInputStream(fis)) {
			final byte[] buffer = new byte[1024];

			ze = zis.getNextEntry();
			while (ze != null) {
				final String subtitleFilePath = this.downloadFileWriteLocation + ze.getName();

				try (final FileOutputStream fos = new FileOutputStream(subtitleFilePath)) {
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}

				extractedSubtitleFilePathList.add(subtitleFilePath);

				zis.closeEntry();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
		} catch (final IOException e) {
			final String message = "압축된 자막 파일의 압축을 해제하는 중에 예외가 발생하였습니다.(2)";

			log.warn(message, e);
			NotifyApiClient.sendNotifyMessage(message, true);

			return false;
		}

		return true;
	}

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
