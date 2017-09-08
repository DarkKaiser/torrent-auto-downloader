package com.darkkaiser.torrentad.website;

import com.darkkaiser.torrentad.util.RadixNotation62Util;
import org.jsoup.helper.StringUtil;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractWebSiteSearchResultData implements WebSiteSearchResultData {

	protected final String identifier;

	protected final WebSiteBoard board;

	protected final String keyword;

	protected final List<WebSiteBoardItem> results;

	public AbstractWebSiteSearchResultData(final WebSiteBoard board, final String keyword, final List<WebSiteBoardItem> results) {
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
	public Iterator<WebSiteBoardItem> resultIterator(final Comparator<? super WebSiteBoardItem> comparator) {
		if (comparator == null)
			throw new NullPointerException("comparator");

		this.results.sort(comparator);

		return this.results.iterator();
	}

	@Override
	public String toString() {
		return AbstractWebSiteSearchResultData.class.getSimpleName() +
				"{" +
				"identifier:" + getIdentifier() +
				", board:" + getBoard() +
				", keyword:" + getKeyword() +
				"}";
	}

}
