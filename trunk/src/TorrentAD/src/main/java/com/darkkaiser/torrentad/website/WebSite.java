package com.darkkaiser.torrentad.website;

import com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogo;
import com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;
import com.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoSearchContext;
import org.jsoup.helper.StringUtil;

import java.util.Objects;

public enum WebSite {

	BOGOBOGO("보고보고") {
		@Override
		public WebSiteConnection createConnection(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation) {
			return new RetryLoginOnNoPermissionWebSite(new BogoBogo(siteConnector, owner, downloadFileWriteLocation));
		}

		@Override
		public WebSiteSearchContext createSearchContext() {
			return new BogoBogoSearchContext();
		}

		@Override
		public WebSiteBoard getBoardByName(final String name) {
			return BogoBogoBoard.fromString(name);
		}

		@Override
		public WebSiteBoard getBoardByCode(final String code) {
			if (StringUtil.isBlank(code) == true)
				throw new IllegalArgumentException("code는 빈 문자열을 허용하지 않습니다.");

			WebSiteBoard[] boardValues = getBoardValues();
			for (final WebSiteBoard board : boardValues) {
				if (board.getCode().equals(code) == true)
					return board;
			}

			return null;
		}

		@Override
		public WebSiteBoard[] getBoardValues() {
			return BogoBogoBoard.values();
		}
	};

	private String name;
	
	WebSite(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static WebSite fromString(final String name) {
		Objects.requireNonNull(name, "name");

		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final WebSite site : WebSite.values()) {
			if (name.equals(site.getName()) == true)
				return site;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSite.class.getSimpleName(), name));
	}

	public WebSiteAccount createAccount(final String id, final String password) {
		return new DefaultWebSiteAccount(id, password);
	}
	
	public abstract WebSiteConnection createConnection(final WebSiteConnector siteConnector, final String owner, final String downloadFileWriteLocation);
	
	public abstract WebSiteSearchContext createSearchContext();
	
	public WebSiteSearchKeywords createSearchKeywords(final String modeValue) {
		return new DefaultWebSiteSearchKeywords(WebSiteSearchKeywordsMode.fromString(modeValue));
	}

	public abstract WebSiteBoard getBoardByName(final String name);

	public abstract WebSiteBoard getBoardByCode(final String code);

	public abstract WebSiteBoard[] getBoardValues();

	@Override
	public String toString() {
		return getName();
	}

}