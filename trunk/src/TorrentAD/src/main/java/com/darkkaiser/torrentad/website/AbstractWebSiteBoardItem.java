package com.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbstractWebSiteBoardItem implements WebSiteBoardItem {

	// 게시판
	private final WebSiteBoard board;

	// 식별자
	private final long identifier;

	// 제목
	private final String title;

	// 등록일자
	private final Date registDate;
	private final DateFormat registDateFormat;

	// 첨부파일에 대한 다운로드 링크 목록
	private final List<WebSiteBoardItemDownloadLink> downloadLinks = new ArrayList<>();

	protected AbstractWebSiteBoardItem(final WebSiteBoard board, final long identifier, final String title, final String registDateString) throws ParseException {
		Objects.requireNonNull(board, "board");

		if (identifier == WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE)
			throw new InvalidBoardItemIdentifierValueException();
		if (StringUtil.isBlank(title) == true)
			throw new IllegalArgumentException("title은 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(registDateString) == true)
			throw new IllegalArgumentException("registDateString은 빈 문자열을 허용하지 않습니다.");

		this.board = board;
		this.title = title;
		this.identifier = identifier;
		this.registDateFormat = new SimpleDateFormat(this.board.getDefaultRegistDateFormatString());
		this.registDate = this.registDateFormat.parse(registDateString);
	}

	@Override
	public WebSiteBoard getBoard() {
		return this.board;
	}

	@Override
	public long getIdentifier() {
		return this.identifier;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public Date getRegistDate() {
		return this.registDate;
	}

	@Override
	public String getRegistDateString() {
		return this.registDateFormat.format(this.registDate);
	}
	
	@Override
	public void addDownloadLink(final WebSiteBoardItemDownloadLink downloadLink) {
        Objects.requireNonNull(downloadLink, "downloadLink");

		this.downloadLinks.add(downloadLink);
	}

	@Override
	public void clearDownloadLink() {
		this.downloadLinks.clear();
	}

	@Override
	public Iterator<WebSiteBoardItemDownloadLink> downloadLinkIterator() {
		return this.downloadLinks.iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(AbstractWebSiteBoardItem.class.getSimpleName())
				.append("{")
				.append("board:").append(getBoard())
				.append(", identifier:").append(getIdentifier())
				.append(", title:").append(getTitle())
				.append(", registDate:").append(getRegistDateString())
				.append(", downloadLinks:");

		boolean firstKeyword = true;
		Iterator<WebSiteBoardItemDownloadLink> iterator = this.downloadLinks.iterator();
		while (iterator.hasNext()) {
			if (firstKeyword == false) {
				sb.append("|")
				  .append(iterator.next());
			} else {
				firstKeyword = false;
				sb.append(iterator.next());
			}
		}

		return sb.append("}").toString();
	}

}