package com.darkkaiser.torrentad.website.impl.torrentqq;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.website.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class TorrentQQ extends AbstractWebSite {

    public TorrentQQ(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
		super(siteConnector, owner, WebSite.TORRENTQQ, downloadFileWriteLocation);
	}

    @Override
    protected void login0(final WebSiteAccount account) throws Exception {
    	try {
			// 도메인 리다이렉션 여부를 확인한다.
			// 리다이렉션 되는 경우 토렌트 사이트 URL을 리다이렉션 되는 URL로 변경해준다.
			checkDomainRedirection();
		} catch (final IOException e) {
			log.warn("도메인 리다이렉션 여부를 확인하는 중에 예외가 발생하였습니다.", e);
		}
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
			if (responseURL.endsWith("/") == true)
				responseURL = responseURL.substring(0, responseURL.length() - 1);

			if (baseURL.equals(responseURL) == false) {
				setBaseURL(responseURL);

				// 도메인 변경사항을 파일에 반영한다.
				final List<String> lines = Files.readAllLines(Paths.get(Constants.APP_CONFIG_FILE_NAME), StandardCharsets.UTF_8);

				boolean finded = false;
				for (int i = 0; i < lines.size(); i++) {
					if (lines.get(i).contains(Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL) == true) {
						finded = true;
						lines.set(i, "\t\t\t<" + Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL + ">" + responseURL + "</" + Constants.APP_CONFIG_TAG_WEBSITE_BASE_URL + ">");
						break;
					}
				}

				if (finded == true)
					Files.write(Paths.get(Constants.APP_CONFIG_FILE_NAME), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			}
		}
    }

    @Override
	protected String getSearchQueryString(final String keyword) {
		return String.format("&q=%s", keyword);
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
			final TorrentQQBoard siteBoard = (TorrentQQBoard) board;

			for (int page = 1; page <= siteBoard.getDefaultLoadPageCount(); ++page) {
				if (StringUtil.isBlank(_queryString) == true)
					url = String.format("%s%s?&page=%d&%s", getBaseURL(), siteBoard.getPath(), page, _queryString);
				else
					url = String.format("%s/search?%s&page=%d&sm=top.s&board=%s", getBaseURL(), _queryString, page, siteBoard.getCode());

				Connection.Response boardItemsResponse = Jsoup.connect(url)
						.userAgent(USER_AGENT)
		                .method(Connection.Method.GET)
		                .timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
		                .execute();

				if (boardItemsResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + url + " returned " + boardItemsResponse.statusCode() + ": " + boardItemsResponse.statusMessage());

				final Document boardItemsDoc = boardItemsResponse.parse();
				final Elements elements = boardItemsDoc.select("div.list-board > ul.list-body > li.list-item");

				if (elements.isEmpty() == true) {
					throw new ParseException(String.format("게시판의 추출된 게시물이 0건입니다. CSS셀렉터를 확인하세요.(URL:%s)", url), 0);
				} else {
					try {
						for (final Element element : elements) {
                            final Iterator<Element> iterator = element.children().iterator();

							// 번호
							final String no = iterator.next().text();
							if (no.contains("일치하는 검색결과가 없습니다."/* 페이지에 게시물이 0건인 경우... */) == true)
								continue;

							String registDate;
							Element titleAreaElement;

							if (board == TorrentQQBoard.MOVIE) {
								// 썸네일
								iterator.next();

								// 제목
								titleAreaElement = iterator.next();

								// 날짜
								final Elements spanElements = titleAreaElement.select("div.item-details > span");
								if (spanElements.size() != 4)
									throw new ParseException(String.format("게시물 제목의 <SPAN> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

								registDate = spanElements.get(spanElements.size() - 1).text().trim();
							} else if (board == TorrentQQBoard.BROADCASTING || board == TorrentQQBoard.ANIMATION) {
								// 제목
								titleAreaElement = iterator.next();

								// 용량
								iterator.next();

								// 날짜
								registDate = iterator.next().text().trim();
							} else {
								throw new IllegalArgumentException(String.format("지원하지 않는 토렌트 게시판(%s)입니다.", board));
							}

							//
							// 제목 분석
							//
							final Elements titleLinkElements = titleAreaElement.select("a[title]");
							if (titleLinkElements.size() != 1)
								throw new ParseException(String.format("게시물 제목의 <A> 태그의 갯수가 유효하지 않습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

							final String detailPageURL = titleLinkElements.get(0).attr("href");
							if (detailPageURL.startsWith(String.format("%s/torrent/", getBaseURL())) == false)
								throw new ParseException(String.format("게시물 상세페이지의 URL 추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

							final String title = trimString(titleLinkElements.get(titleLinkElements.size() - 1).attr("title"));

							final int pos1 = detailPageURL.lastIndexOf("/");
							final int pos2 = detailPageURL.lastIndexOf(".html");
							if (pos1 == -1 || pos2 == -1 || pos1 >= pos2)
								throw new ParseException(String.format("게시물 상세페이지의 ID추출이 실패하였습니다. CSS셀렉터를 확인하세요.(URL:%s)\r\nHTML:%s", url, titleAreaElement.html()), 0);

							final String identifier = detailPageURL.substring(pos1 + 1, pos2);

							//
							// 날짜 분석
							//
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

							boardItems.add(new DefaultWebSiteBoardItem(siteBoard, Long.parseLong(identifier), title, registDate, detailPageURL));
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

		final String detailPageURL = boardItem.getDetailPageURL();
		if (StringUtil.isBlank(detailPageURL) == true) {
			log.error(String.format("게시물의 상세페이지 URL이 빈 문자열이므로, 첨부파일에 대한 정보를 로드할 수 없습니다.(%s)", boardItem));
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
			final Elements elements = detailPageDoc.select("div.view-wrap > section > article a");

			if (elements.isEmpty() == true) {
				throw new ParseException(String.format("게시물에서 추출된 첨부파일에 대한 정보가 0건입니다. CSS 셀렉터를 확인하세요.(URL:%s)", detailPageURL), 0);
			} else {
				try {
					String[] exceptFileExtension = { "JPG", "JPEG", "GIF", "PNG" };

					for (final Element element : elements) {
						if (element.hasClass("btn-magnet"/* 마그넷링크 */) == true || element.hasClass("btn-download"/* 다운로드로 바로가기 */) == true)
							continue;

						String fileName;
						String link = element.attr("href");
						if (element.hasClass("btn-torrent"/* 토렌트 */) == true) {
                            /*
                             * 토렌트 파일인 경우...
                             */
                            link = String.format("%s%s", getBaseURL(), link);

							fileName = String.format("%s.torrent", link.substring(link.lastIndexOf("/") + 1));
						} else {
                            /*
                             * 자막등의 파일인 경우...
                             */
                            fileName = trimString(element.text());
                            fileName = trimString(fileName.substring(0, fileName.lastIndexOf("(")));
						}

						// 특정 파일은 다운로드 받지 않도록 한다.
						if (Arrays.asList(exceptFileExtension).contains(fileName.toUpperCase()) == true)
							continue;

						boardItem.addDownloadLink(TorrentQQBoardItemDownloadLinkImpl.newInstance(link, fileName));
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
		final String detailPageURL = boardItem.getDetailPageURL();

		assert StringUtil.isBlank(detailPageURL) == false;

		final Iterator<WebSiteBoardItemDownloadLink> iterator = boardItem.downloadLinkIterator();
		while (iterator.hasNext() == true) {
			final TorrentQQBoardItemDownloadLink downloadLink = (TorrentQQBoardItemDownloadLink) iterator.next();
			if (downloadLink.isDownloadable() == false || downloadLink.isDownloadCompleted() == true)
				continue;

			++downloadTryCount;

			log.info("검색된 게시물('{}')의 첨부파일을 다운로드합니다.({})", boardItem.getTitle(), downloadLink);

			try {
				/*
				  Cookie 데이터가 필요하므로 한번 더 상세 페이지를 로드한다.
				 */
				final Connection.Response detailPageResponse = Jsoup.connect(detailPageURL)
						.userAgent(USER_AGENT)
						.method(Connection.Method.GET)
						.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
						.execute();

				if (detailPageResponse.statusCode() != HttpStatus.SC_OK)
					throw new IOException("GET " + detailPageURL + " returned " + detailPageResponse.statusCode() + ": " + detailPageResponse.statusMessage());

				// 다운로드 받는 파일의 이름을 구한다.
				final String downloadFilePath = String.format("%s%s", this.downloadFileWriteLocation, downloadLink.getFileName().trim());
				final File downloadFile = new File(downloadFilePath);
				if (downloadFile.exists() == true) {
					log.error("동일한 이름을 가진 파일이 이미 존재합니다. 해당 파일의 다운로드는 중지됩니다.({})", downloadFilePath);
					continue;
				}

				final String downloadLinkPage = downloadLink.getLink();

				if (downloadFilePath.endsWith(".torrent") == true) {
					/*
					  파일을 바로 다운로드 하거나, FileTender 다운로드 페이지 URL을 구한다.
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

					final String fileTenderPageURL = downloadProcessResponse.url().toString();
					if (fileTenderPageURL.contains(".filetender.") == true) {
						/*
						  FileTender 다운로드 페이지 열기
						 */
						final Connection.Response fileTenderPageResponse = Jsoup.connect(fileTenderPageURL)
								.userAgent(USER_AGENT)
								.header("Referer", detailPageURL)
								.method(Connection.Method.GET)
								.cookies(detailPageResponse.cookies())
								.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
								.ignoreContentType(true)
								.execute();

						if (fileTenderPageResponse.statusCode() != HttpStatus.SC_OK)
							throw new IOException("POST " + downloadLinkPage + " returned " + fileTenderPageResponse.statusCode() + ": " + fileTenderPageResponse.statusMessage());

						final Document fileTenderPageDoc = fileTenderPageResponse.parse();

						final String key = fileTenderPageDoc.select("input[name=key]").val();
						final String ticket = fileTenderPageDoc.select("input[name=Ticket]").val();
						final String randStr = fileTenderPageDoc.select("input[name=Randstr]").val();
						final String userIP = fileTenderPageDoc.select("input[name=UserIP]").val();

						if (StringUtil.isBlank(key) == true || StringUtil.isBlank(userIP) == true)
							throw new ParseException(String.format("첨부파일을 다운로드 하기 위한 작업 진행중에 수신된 데이터의 값이 유효하지 않습니다. CSS 셀렉터를 확인하세요.(URL:%s, key:%s, Ticket:%s, Randstr:%s, UserIP:%s)", downloadLinkPage, key, ticket, randStr, userIP), 0);

						final String fileTenderPageDocHtml = fileTenderPageDoc.html();
						final int pos1 = fileTenderPageDocHtml.indexOf("var newUrl = '");
						final int pos2 = fileTenderPageDocHtml.indexOf("'", pos1 + "var newUrl = '".length());
						if (pos1 == -1 || pos2 == -1)
							throw new ParseException(String.format("첨부파일을 다운로드 하기 위한 작업 진행중에 수신된 데이터의 값이 유효하지 않습니다. CSS 셀렉터를 확인하세요.(URL:%s)", downloadLinkPage), 0);

						final String fileTenderDownloadURL = fileTenderPageDocHtml.substring(pos1 + "var newUrl = '".length(), pos2);

						/*
						  첨부파일 다운로드 하기
						 */
						downloadProcessResponse = Jsoup.connect(fileTenderDownloadURL)
								.userAgent(USER_AGENT)
								.header("Referer", detailPageURL)
								.data("key", key)
								.data("Ticket", ticket)
								.data("Randstr", randStr)
								.data("UserIP", userIP)
								.method(Connection.Method.GET)
								.cookies(fileTenderPageResponse.cookies())
								.timeout(URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND)
								.ignoreContentType(true)
								.execute();

						if (downloadProcessResponse.statusCode() != HttpStatus.SC_OK)
							throw new IOException("POST " + fileTenderDownloadURL + " returned " + downloadProcessResponse.statusCode() + ": " + downloadProcessResponse.statusMessage());
					}

					/*
					  첨부파일 저장
					 */
					final File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION);

					final FileOutputStream fos = new FileOutputStream(notyetDownloadFile);
					fos.write(downloadProcessResponse.bodyAsBytes());
					fos.close();

					notyetDownloadFile.renameTo(downloadFile);
				} else {
					final File notyetDownloadFile = new File(downloadFilePath + Constants.AD_SERVICE_TASK_NOTYET_DOWNLOAD_FILE_EXTENSION);

					/*
					  첨부파일 다운로드 하기
					 */
					final Connection.Response downloadProcessResponse = Jsoup.connect(downloadLinkPage)
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
					final FileOutputStream fos = new FileOutputStream(notyetDownloadFile);
					fos.write(downloadProcessResponse.bodyAsBytes());
					fos.close();

					notyetDownloadFile.renameTo(downloadFile);
				}

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
		return TorrentQQ.class.getSimpleName() +
                "{" +
                "}, " +
                super.toString();
	}

}
