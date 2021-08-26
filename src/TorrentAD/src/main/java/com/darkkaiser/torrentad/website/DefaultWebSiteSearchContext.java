package com.darkkaiser.torrentad.website;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;

import java.util.*;

@Slf4j
public class DefaultWebSiteSearchContext implements WebSiteSearchContext {

	private final WebSite site;

	private final Map<WebSiteSearchKeywordsType, List<WebSiteSearchKeywords>> searchKeywords = new HashMap<>();

	@Getter
	private WebSiteBoard board;

	@Getter
	@Setter
	private long latestDownloadBoardItemIdentifier = WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE;

	public DefaultWebSiteSearchContext(final WebSite site) {
		Objects.requireNonNull(site, "site");

		this.site = site;

		for (final WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			this.searchKeywords.put(type, new ArrayList<>());
	    }
	}

	@Override
	public WebSite getWebSite() {
		return this.site;
	}

	@Override
	public void setBoardName(final String name) {
		this.board = this.site.getBoardByName(name);
	}

	@Override
	public void addSearchKeywords(final WebSiteSearchKeywordsType type, final WebSiteSearchKeywords searchKeywords) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(searchKeywords, "searchKeywords");

		this.searchKeywords.get(type).add(searchKeywords);
	}

	@Override
	public boolean isSatisfySearchCondition(final WebSiteSearchKeywordsType type, final String text) {
        Objects.requireNonNull(type, "type");

		if (StringUtil.isBlank(text) == true)
			throw new IllegalArgumentException("text는 빈 문자열을 허용하지 않습니다.");

		Iterator<WebSiteSearchKeywords> iterator = this.searchKeywords.get(type).iterator();
		if (iterator.hasNext() == true) {
			while (iterator.hasNext()) {
				if (iterator.next().isSatisfySearchCondition(text) == false)
					return false;
			}
			
			return true;
		} else {
			return type.allowEmpty();
		}
	}

	@Override
	public void validate() {
        Objects.requireNonNull(site, "site");

		for (final WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			if (type.allowEmpty() == false) {
				if (this.searchKeywords.get(type).isEmpty() == true)
					throw new EmptySearchKeywordsException(String.format("등록된 검색 키워드 목록이 없습니다.(%s)", type.getValue()));
			}
		}

		Objects.requireNonNull(this.board, "board");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (final Exception e) {
			log.debug(null, e);
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(DefaultWebSiteSearchContext.class.getSimpleName())
				.append("{")
				.append("site:").append(getWebSite())
				.append(", searchKeywords:");

		boolean firstKeywordsType = true;
		for (final WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			if (firstKeywordsType == false) {
				sb.append(", ")
				  .append(type.getValue())
				  .append("[");
			} else {
				firstKeywordsType = false;
				sb.append(type.getValue())
				  .append("[");
			}

			boolean firstKeywords = true;
			Iterator<WebSiteSearchKeywords> iterator = this.searchKeywords.get(type).iterator();
			while (iterator.hasNext()) {
				if (firstKeywords == false) {
					sb.append(",")
					  .append(iterator.next());
				} else {
					firstKeywords = false;
					sb.append(iterator.next());
				}
			}
			
			sb.append("]");
	    }

		sb.append(", board:").append(getBoard())
		  .append(", latestDownloadBoardItemIdentifier:").append(getLatestDownloadBoardItemIdentifier())
		  .append("}");

		return sb.toString();
	}

}
