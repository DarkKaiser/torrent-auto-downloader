package kr.co.darkkaiser.torrentad.website.board;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.jsoup.helper.StringUtil;

public abstract class AbstractWebSiteBoardItem implements WebSiteBoardItem {

	// 식별자
	private int identifier;

	// 제목
	private String title;

	// 등록일자
	private Date registDate;

	protected AbstractWebSiteBoardItem(int identifier, String title, String registDateString) throws ParseException {
		if (StringUtil.isBlank(title) == true) {
			throw new IllegalArgumentException("title은 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(registDateString) == true) {
			throw new IllegalArgumentException("registDateString은 빈 문자열을 허용하지 않습니다.");
		}

		this.title = title;
		this.identifier = identifier;
		// @@@@@ 초기화되기 전에 사용됨 널에러바랭
//		this.registDate = getRegistDateFormat().parse(registDateString);
	}
	
	protected void init() {
		// @@@@@
//		this.registDate = getRegistDateFormat().parse(registDateString);
	}

	@Override
	public int getIdentifier() {
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
		return getRegistDateFormat().format(this.registDate);
	}

	protected abstract DateFormat getRegistDateFormat();

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractWebSiteBoardItem.class.getSimpleName())
				.append("{")
				.append("identifier:").append(this.identifier)
				.append(", title:").append(this.title)
				.append(", registDate:").append(getRegistDateString())
				.append("}")
				.toString();
	}

}
