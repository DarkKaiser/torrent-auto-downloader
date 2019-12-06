package com.darkkaiser.torrentad.website.impl.torrenthall;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TorrentHall extends AbstractWebSite {

	private static final Logger logger = LoggerFactory.getLogger(TorrentHall.class);

	private static final String BASE_URL = "https://torrenthall1.com";
	public static final String BASE_URL_WITH_DEFAULT_PATH = String.format("%s/board", BASE_URL);

	public TorrentHall(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.TORRENTHALL, downloadFileWriteLocation);
	}

	@Override
	protected String getSearchQueryString(final String keyword) {
		return String.format("&skeyword=%s", keyword);
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
			final TorrentHallBoard siteBoard = (TorrentHallBoard) board;

			for (int page = 1; page <= siteBoard.getDefaultLoadPageCount(); ++page) {
				url = String.format("%s?&page=%d&%s", siteBoard.getURL(), page, _queryString);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				Document boardItemsDoc = boardItemsResponse.parse();
				Elements elements = boardItemsDoc.select("div.list-board > ul.list-body > li");

				if (elements.isEmpty() == true) {
					throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
				} else {
					try {
						for (final Element element : elements) {
							final Iterator<Element> iterator = element.children().iterator();

							//
							// 번호
							//
							final String no = iterator.next().text();
							if (no.contains("게시물이 없습니다"/* 페이지에 게시물이 0건인 경우... */) == true)
								continue;

							//
							// 제목
							//
							final Element titleElement = iterator.next();

							final Elements titleLinkElement = titleElement.getElementsByTag("a");
							if (titleLinkElement.size() != 1)
								throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

							String detailPageURL = titleLinkElement.get(titleLinkElement.size() - 1).attr("href");
							if (detailPageURL.startsWith(String.format("%s/post/", TorrentHall.BASE_URL)) == false)
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

							final String title = trimString(titleLinkElement.get(titleLinkElement.size() - 1).text());

							int pos = detailPageURL.lastIndexOf("?");
							if (pos == -1)
								pos = detailPageURL.length();

							final String identifier = detailPageURL.substring(String.format("%s/post/", TorrentHall.BASE_URL).length(), pos);

							//
							// 용량
							//
							iterator.next();

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
								throw new ParseException(String.format("게시물의 날짜 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);
							}

							boardItems.add(new DefaultWebSiteBoardItem(siteBoard, Long.parseLong(identifier), title, registDate, detailPageURL));
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
			Elements elements = detailPageDoc.select("div.view-wrap > section > article a");

			if (elements.isEmpty() == true) {
				throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
			} else {
				try {
					String[] exceptFileExtension = { "JPG", "JPEG", "GIF", "PNG" };

					for (final Element element : elements) {
                        String fileName = null;
						String link = element.attr("href");
                        if (link.equals("javascript:;") == true) {
                            /*
                             * 토렌트 파일인 경우...
                             */
                            link = element.attr("onclick");
                        } else {
                            /*
                             * 자막등의 파일인 경우...
                             */
                            fileName = trimString(element.text());
                            fileName = trimString(fileName.substring(0, fileName.lastIndexOf("(")));
                        }

                        int pos1 = link.indexOf("file_download('");
                        if (pos1 == -1)
                            continue;

                        pos1 += "file_download('".length();
                        final int pos2 = link.indexOf("'", pos1);
                        if (pos2 == -1)
                            continue;

                        link = link.substring(pos1, pos2);
                        if (link.startsWith("magnet:") == true)
                            continue;

                        if (fileName == null)
                            fileName = String.format("%s.torrent", link.substring(link.lastIndexOf("/") + 1));

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtension).contains(fileName.toUpperCase()) == true)
							continue;

						boardItem.addDownloadLink(TorrentHallBoardItemDownloadLinkImpl.newInstance(link, fileName));
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
			TorrentHallBoardItemDownloadLink downloadLink = (TorrentHallBoardItemDownloadLink) iterator.next();
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

				final String downloadLinkPage = downloadLink.getLink();

                /*
                  홈페이지의 Cookie 데이터가 필요하므로 한번 더 상세 페이지를 로드한다.
                 */
                Connection.Response detailPageResponse = Jsoup.connect(detailPageURL)
                        .userAgent(USER_AGENT)
                        .method(Connection.Method.GET)
                        .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
                        .execute();

                if (detailPageResponse.statusCode() != HttpStatus.SC_OK)
                    throw new IOException("GET " + detailPageURL + " returned " + detailPageResponse.statusCode() + ": " + detailPageResponse.statusMessage());

                File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOADED_FILE_EXTENSION);

                /*
                  첨부파일 다운로드 하기
                 */
                Connection.Response downloadProcessResponse = Jsoup.connect(downloadLinkPage)
                        .userAgent(USER_AGENT)
                        .method(Connection.Method.GET)
                        .cookies(detailPageResponse.cookies())
                        .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
                        .ignoreContentType(true)
                        .execute();

                if (downloadProcessResponse.statusCode() != HttpStatus.SC_OK)
                    throw new IOException("POST " + downloadLinkPage + " returned " + downloadProcessResponse.statusCode() + ": " + downloadProcessResponse.statusMessage());

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
		return TorrentHall.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
