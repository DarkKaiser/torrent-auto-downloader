package kr.co.darkkaiser.torrentad.website;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.util.RadixNotation62Util;

public abstract class AbstractWebSiteSearchHistoryData implements WebSiteSearchHistoryData {

	protected final String identifier;

	protected final WebSiteBoard board;

	protected final String keyword;

	protected final List<WebSiteBoardItem> results;

	public AbstractWebSiteSearchHistoryData(WebSiteBoard board, String keyword, List<WebSiteBoardItem> results) {
		if (board == null)
			throw new NullPointerException("board");
		if (results == null)
			throw new NullPointerException("results");
		if (StringUtil.isBlank(keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");

		this.board = board;
		this.keyword = keyword;
		this.results = results;
		this.identifier = RadixNotation62Util.toString(System.currentTimeMillis());
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public WebSiteBoard getBoard() {
		return this.board;
	}

	@Override
	public String getKeyword() {
		return this.keyword;
	}
	
	@Override
	public Iterator<WebSiteBoardItem> resultIterator(Comparator<? super WebSiteBoardItem> comparator) {
		if (comparator == null)
			throw new NullPointerException("comparator");

		Collections.sort(this.results, comparator);

		return this.results.iterator();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractWebSiteSearchHistoryData.class.getSimpleName())
				.append("{")
				.append("identifier:").append(getIdentifier())
				.append(", board:").append(getBoard())
				.append(", keyword:").append(getKeyword())
				.append("}")
				.toString();
	}

}
