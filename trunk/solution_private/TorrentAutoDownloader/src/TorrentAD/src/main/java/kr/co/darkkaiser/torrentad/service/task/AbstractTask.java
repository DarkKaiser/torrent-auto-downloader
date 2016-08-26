package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.data.BogoBogoWebSiteTaskContext;
import kr.co.darkkaiser.torrentad.website.data.WebSiteSearchContext;

// @@@@@
public abstract class AbstractTask implements Task {
	
	protected final WebSite site;
	
	// 검색조건
	protected final WebSiteSearchContext searchContext;

	public AbstractTask(WebSite site) {
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.site = site;
		
		// bogobogo와는 상관없이 생성
		this.searchContext = new BogoBogoWebSiteTaskContext();
	}

	// @@@@@
	@Override
	public void setBoardName(String boardName) {
		if (boardName == null) {
			throw new NullPointerException("boardName");
		}
		
		searchContext.setBoardName(boardName);
	}

}
