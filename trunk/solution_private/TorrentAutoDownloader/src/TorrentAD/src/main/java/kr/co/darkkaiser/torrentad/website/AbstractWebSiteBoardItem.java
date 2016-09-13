package kr.co.darkkaiser.torrentad.website;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.helper.StringUtil;

public abstract class AbstractWebSiteBoardItem implements WebSiteBoardItem {

	private final WebSiteBoard board;

	// 식별자
	private final long identifier;

	// 제목
	private final String title;

	// 등록일자
	private final Date registDate;
	private final DateFormat registDateFormat;

	protected AbstractWebSiteBoardItem(WebSiteBoard board, long identifier, String title, String registDateString) throws ParseException {
		if (board == null)
			throw new NullPointerException("board");
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
	public String toString() {
		return new StringBuilder()
				.append(AbstractWebSiteBoardItem.class.getSimpleName())
				.append("{")
				.append("board:").append(this.board)
				.append(", identifier:").append(this.identifier)
				.append(", title:").append(this.title)
				.append(", registDate:").append(getRegistDateString())
				.append("}")
				.toString();
	}

}
