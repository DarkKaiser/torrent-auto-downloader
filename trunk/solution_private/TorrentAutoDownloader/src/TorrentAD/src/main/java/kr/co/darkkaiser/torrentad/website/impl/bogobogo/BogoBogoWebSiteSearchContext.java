package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;

public class BogoBogoWebSiteSearchContext implements WebSiteSearchContext {

	@Override
	public void setBoardName(String boardName) {
		if (boardName == null) {
			throw new NullPointerException("boardName");
		}
		if (StringUtil.isBlank(boardName) == true) {
			throw new IllegalArgumentException("boardName must not be empty.");
		}

	}

	// @@@@@
	
}
