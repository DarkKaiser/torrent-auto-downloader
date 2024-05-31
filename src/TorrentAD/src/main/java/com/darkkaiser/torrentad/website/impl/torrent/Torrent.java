package com.darkkaiser.torrentad.website.impl.torrent;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.util.notifyapi.NotifyApiClient;
import com.darkkaiser.torrentad.website.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class Torrent extends AbstractWebSite {

    public Torrent(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.TORRENT, downloadFileWriteLocation);
	}

    @Override
	protected String getSearchQueryString(final String keyword) {
		return String.format("keywords=%s", keyword);
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
			final TorrentBoard siteBoard = (TorrentBoard) board;

			String loadBoardItemsSelectQuery = "div.container div.topic-item";
			if (siteBoard.hasCategory() == true)
				loadBoardItemsSelectQuery = "div.container div.flex-grow.content-center";

			for (int page = 1; page <= siteBoard.getDefaultLoadPageCount(); ++page) {
				if (StringUtil.isBlank(_queryString) == true)
					url = String.format("%s%s?page=%d", getBaseURL(), siteBoard.getPath(), page);
				else
					url = String.format("%s/search/index?%s&search_type=0&page=%d", getBaseURL(), _queryString, page);

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				final Document boardItemsDoc = boardItemsResponse.parse();
				final Elements elements = boardItemsDoc.select(loadBoardItemsSelectQuery);

				if (elements.isEmpty() == false) {
					try {
						for (final Element element : elements) {
							String registDate;
							Elements titleLinkElement;

							if (siteBoard.hasCategory() == true) {
								// 제목
								titleLinkElement = element.select("div > a");

								if (titleLinkElement.size() != 1)
									throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, element.html()), 0);

								// 등록일
								final Elements registElement = element.select("div > i.fa-clock");

								if (registElement.size() != 1)
									throw new ParseException(String.format("게시물 생성일의 <I> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, element.html()), 0);

								final String[] splitValues = registElement.get(0).parent().text().trim().split(" ");
								registDate = splitValues[splitValues.length - 2].replace("/", ".").trim();
							} else {
								final Iterator<Element> iterator = element.children().iterator();

								// 번호
								iterator.next();

								// 제목
								final Element titleElement = iterator.next();

								titleLinkElement = titleElement.getElementsByTag("a");
								if (titleLinkElement.size() != 1)
									throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleElement.html()), 0);

								// 용량
								iterator.next().text();

								// 등록일
								registDate = iterator.next().text().trim();
							}

							//
							// 제목
							//
							final String title = trimString(titleLinkElement.text());

							final String detailPageURL = titleLinkElement.attr("href");
							if (detailPageURL.isEmpty() == true)
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleLinkElement.html()), 0);

							final int noPos = detailPageURL.lastIndexOf("/");
							if (noPos == -1)
								throw new ParseException(String.format("게시물 상세페이지의 ID추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleLinkElement.html()), 0);

							final String identifier = detailPageURL.substring(noPos + 1);

							//
							// 등록일
							//
							if (registDate.matches("^[0-9]{2}.[0-9]{2}$"/* 게시물이 등록된 일자 */) == true) {
								final String[] daySplit = registDate.split("\\.");

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
								throw new ParseException(String.format("게시물의 날짜 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\n등록일:%s", url, registDate), 0);
							}

							boardItems.add(new DefaultWebSiteBoardItem(siteBoard, Long.parseLong(identifier), title, registDate, String.format("%s%s", getBaseURL(), detailPageURL)));
						}
					} catch (final NoSuchElementException e) {
						final String message = String.format("게시물을 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, elements.html());

						log.error(message, e);
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
			if (e instanceof SocketTimeoutException == false) {
				final String message = String.format("게시판(%s) 데이터를 로드하는 중에 예외가 발생하였습니다.(URL:%s)", board, url);

				log.error(message, e);
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

			log.error(message);
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
			final Elements elements = detailPageDoc.select("div.container div.mt-2 div.border-b a.border > i.fa-download");

			if (elements.isEmpty() == true) {
				throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS 셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
			} else {
				try {
					final String[] exceptFileExtensions = { ".JPG", ".JPEG", ".GIF", ".PNG" };

					int downloadLinkCount = 0;
					for (final Element element : elements) {
						final String link = element.parent().attr("href");
						String fileName = String.format("%s (%d).torrent", boardItem.getTitle(), ++downloadLinkCount);

						if (element.parent() != null && element.parent().parent() != null && element.parent().parent().parent() != null) {
							try {
								final Element previousElementSibling = element.parent().parent().parent().previousElementSibling();
                                if (previousElementSibling != null) {
									// 파일명을 출력하고 있는 태그인지 확인한다.
									if (previousElementSibling.hasClass("border-t") == true) {
										final String value = previousElementSibling.text().trim();
										if (value.toLowerCase().endsWith(".torrent") == false) {
											fileName = value;
										}
									}
                                }
							} catch (final NullPointerException ignored) {
							}
						}

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtensions).contains(fileName.substring(fileName.lastIndexOf(".")).toUpperCase()) == true)
							continue;

						boardItem.addDownloadLink(new DefaultWebSiteBoardItemDownloadLink(String.format("%s%s", getBaseURL(), link), fileName));
					}
				} catch (final NoSuchElementException e) {
					final String message = String.format("게시물에서 첨부파일에 대한 정보를 추출하는 중에 예외가 발생하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", detailPageURL, elements.html());

					log.error(message, e);
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

			log.error(message, e);
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

			log.info("검색된 게시물('{}')의 첨부파일을 다운로드합니다.({})", boardItem.getTitle(), downloadLink);

			try {
				// 다운로드 받는 파일의 이름을 구한다.
				final String downloadFilePath = String.format("%s%s", this.downloadFileWriteLocation, downloadLink.getFileName().trim().replace("/", ""));
				final File downloadFile = new File(downloadFilePath);
				if (downloadFile.exists() == true) {
					log.warn("동일한 이름을 가진 파일이 이미 존재합니다. 해당 파일의 다운로드는 중지됩니다.({})", downloadFilePath);
					continue;
				}

				final File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION);

				/*
				  첨부파일 다운로드
				 */
				final String downloadLinkPage = downloadLink.getLink();
				Connection.Response downloadProcessResponse = Jsoup.connect(downloadLinkPage)
						.userAgent(USER_AGENT)
						.method(Connection.Method.GET)
						.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
						.ignoreContentType(true)
						.execute();

				if (downloadProcessResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + downloadLinkPage + " returned " + downloadProcessResponse.statusCode() + ": " + downloadProcessResponse.statusMessage());

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
					extractedSubtitleFilePathList.forEach(filePath -> sb.append("\n\t> 압축 해제된 자막 파일 : ").append(filePath));

					log.info(sb.toString());
				} else {
					notyetDownloadFile.renameTo(downloadFile);

					++downloadCompletedCount;
					downloadLink.setDownloadCompleted(true);

					log.info("검색된 게시물('{}')의 첨부파일 다운로드가 완료되었습니다.({})", boardItem.getTitle(), downloadFilePath);
				}
			} catch (final Exception e) {
				final String message = String.format("첨부파일 다운로드 중에 예외가 발생하였습니다.(%s, %s)", boardItem, downloadLink);

				log.error(message, e);
				NotifyApiClient.sendNotifyMessage(message, true);
			}
		}

		return new Tuple<>(downloadTryCount, downloadCompletedCount);
	}

	@Override
	public String toString() {
		return Torrent.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
