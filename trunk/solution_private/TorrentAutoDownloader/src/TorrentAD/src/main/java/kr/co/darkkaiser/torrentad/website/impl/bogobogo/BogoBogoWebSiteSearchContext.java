package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;

public class BogoBogoWebSiteSearchContext implements WebSiteSearchContext {
	
	private BogoBogoBoard b;

	@Override
	public void setBoardName(String boardName) {
		if (boardName == null) {
			throw new NullPointerException("boardName");
		}
		if (StringUtil.isBlank(boardName) == true) {
			throw new IllegalArgumentException("boardName은 빈 문자열을 허용하지 않습니다.");
		}

		// @@@@@
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	// @@@@@
	
}
