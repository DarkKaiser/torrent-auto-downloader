package com.darkkaiser.torrentad.website.impl.todawa;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.util.notifyapi.NotifyApiClient;
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
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Todawa extends AbstractWebSite {

	private static final Logger logger = LoggerFactory.getLogger(Todawa.class);

	private static final String FILETENDER_DOWNLOAD_URL = "https://file.filetender.com/file.php";

    public Todawa(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.TODAWA, downloadFileWriteLocation);
	}

    @Override
	protected String getSearchQueryString(final String keyword) {
		return keyword;
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
			final TodawaBoard siteBoard = (TodawaBoard) board;

			for (int page = 1; page <= siteBoard.getDefaultLoadPageCount(); ++page) {
				if (StringUtil.isBlank(_queryString) == true)
					url = String.format("%s/list-%s-p%d.html", getBaseURL(), siteBoard.getCode(), page);
				else
					url = String.format("%s/search-%s-%s-p%d.html", getBaseURL(), siteBoard.getCode(), _queryString, page);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				final Document boardItemsDoc = boardItemsResponse.parse();
				final Elements elements = boardItemsDoc.select("div.table-style1 > table > tbody > tr");

				if (elements.isEmpty() == true) {
					// 조회된 게시물이 0건인 경우인지 확인한다.
					Elements elements2 = boardItemsDoc.select("div.table-style1 > table > tbody");
					if (elements2.isEmpty() == false)
						continue;

                    throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
				} else {
					try {
						for (final Element element : elements) {
                            final Iterator<Element> iterator = element.children().iterator();

							// 번호
							iterator.next().text();

							//
							// 제목
							//
							final Element titleAreaElement = iterator.next();

							final Elements titleLinkElements = titleAreaElement.select("a");
							if (titleLinkElements.size() != 2)
								throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

							final String detailPageURL = titleLinkElements.get(1).attr("href");
							if (detailPageURL.startsWith("/view-") == false)
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

							final String title = trimString(titleLinkElements.get(1).text());

							final int pos1 = detailPageURL.lastIndexOf("-");
							final int pos2 = detailPageURL.lastIndexOf(".html");
							if (pos1 == -1 || pos2 == -1 || pos1 >= pos2)
								throw new ParseException(String.format("게시물 상세페이지의 ID추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

							final String identifier = detailPageURL.substring(pos1 + 1, pos2);

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

								registDate = (new SimpleDateFormat(siteBoard.getDefaultRegistDateFormatString())).format(cal.getTime());
							} else {
								throw new ParseException(String.format("게시물의 날짜 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);
							}

							boardItems.add(new DefaultWebSiteBoardItem(siteBoard, Long.parseLong(identifier), title, registDate, String.format("%s%s", getBaseURL(), detailPageURL)));
						}
					} catch (final NoSuchElementException e) {
						final String message = String.format("게시물을 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, elements.html());

						logger.error(message, e);
						NotifyApiClient.sendNotifyMessage(message, true);

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
			if (!(e instanceof SocketTimeoutException)) {
				final String message = String.format("게시판(%s) 데이터를 로드하는 중에 예외가 발생하였습니다.(URL:%s)", board, url);

				logger.error(message, e);
				NotifyApiClient.sendNotifyMessage(message, true);
			}

			return null;
		}

		boardItems.sort(new WebSiteBoardItemComparatorIdentifierAsc());

		return boardItems;
	}

	@Override
	protected boolean loadBoardItemDownloadLink0(final WebSiteBoardItem boardItem) throws NoPermissionException {
		assert boardItem != null;
		assert isLogin() == true;

		final String detailPageURL = boardItem.getDetailPageURL();
		if (StringUtil.isBlank(detailPageURL) == true) {
			final String message = String.format("게시물의 상세페이지 URL이 빈 문자열이므로, 첨부파일에 대한 정보를 로드할 수 없습니다.(%s)", boardItem);

			logger.error(message);
			NotifyApiClient.sendNotifyMessage(message, true);

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

			final Document detailPageDoc = detailPageResponse.parse();
			final Elements elements = detailPageDoc.select("div.file > p > a");

			if (elements.isEmpty() == true) {
				throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS 셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
			} else {
				try {
					final String[] exceptFileExtensions = { ".JPG", ".JPEG", ".GIF", ".PNG" };

					for (final Element element : elements) {
						String link = element.attr("href");
						String fileName = trimString(element.text());

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtensions).contains(fileName.substring(fileName.lastIndexOf(".")).toUpperCase()) == true)
							continue;

						boardItem.addDownloadLink(new DefaultWebSiteBoardItemDownloadLink(link, fileName));
					}
				} catch (final NoSuchElementException e) {
					final String message = String.format("게시물에서 첨부파일에 대한 정보를 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", detailPageURL, elements.html());

					logger.error(message, e);
					NotifyApiClient.sendNotifyMessage(message, true);

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

			final String message = String.format("게시물(%s)의 첨부파일에 대한 정보를 로드하는 중에 예외가 발생하였습니다.", boardItem);

			logger.error(message, e);
			NotifyApiClient.sendNotifyMessage(message, true);

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
		final String detailPageURL = boardItem.getDetailPageURL();

		assert StringUtil.isBlank(detailPageURL) == false;

		final Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			final DefaultWebSiteBoardItemDownloadLink downloadLink = (DefaultWebSiteBoardItemDownloadLink) iterator.next();
			if (downloadLink.isDownloadable() == false || downloadLink.isDownloadCompleted() == true)
				continue;

			++downloadTryCount;

			logger.info("검색된 게시물('{}')의 첨부파일을 다운로드합니다.({})", boardItem.getTitle(), downloadLink);

			try {
				// 다운로드 받는 파일의 이름을 구한다.
				final String downloadFilePath = String.format("%s%s", this.downloadFileWriteLocation, downloadLink.getFileName().trim());
				final File downloadFile = new File(downloadFilePath);
				if (downloadFile.exists() == true) {
					logger.warn("동일한 이름을 가진 파일이 이미 존재합니다. 해당 파일의 다운로드는 중지됩니다.({})", downloadFilePath);
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

				final File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION);

				/*
				  첨부파일 다운로드
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

				// 압축된 자막 파일인 경우 압축을 풀고 삭제한다.
				final List<String> extractedSubtitleFilePathList = new ArrayList<>();
				if (isNotYetDownloadSubtitleZipFile(notyetDownloadFile.getCanonicalPath()) == true &&
						extractNotYetDownloadSubtitleZipFile(notyetDownloadFile.getCanonicalPath(), extractedSubtitleFilePathList) == true) {
					notyetDownloadFile.delete();

					++downloadCompletedCount;
					downloadLink.setDownloadCompleted(true);

					final StringBuilder sb = new StringBuilder(String.format("검색된 게시물('%s')의 첨부파일 다운로드가 완료되었습니다. 다운로드 받은 파일(%s)이 압축된 자막 파일이므로 압축을 해제합니다.", boardItem.getTitle(), downloadFilePath));
					extractedSubtitleFilePathList.forEach(filePath -> {
						sb.append("\n\t> 압축 해제된 자막 파일 : ").append(filePath);
					});

					logger.info(sb.toString());
				} else {
					notyetDownloadFile.renameTo(downloadFile);

					++downloadCompletedCount;
					downloadLink.setDownloadCompleted(true);

					logger.info("검색된 게시물('{}')의 첨부파일 다운로드가 완료되었습니다.({})", boardItem.getTitle(), downloadFilePath);
				}
			} catch (final Exception e) {
				final String message = String.format("첨부파일 다운로드 중에 예외가 발생하였습니다.(%s, %s)", boardItem, downloadLink);

				logger.error(message, e);
				NotifyApiClient.sendNotifyMessage(message, true);
			}
		}

		return new Tuple<>(downloadTryCount, downloadCompletedCount);
	}

	@Override
	public String toString() {
		return Todawa.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
