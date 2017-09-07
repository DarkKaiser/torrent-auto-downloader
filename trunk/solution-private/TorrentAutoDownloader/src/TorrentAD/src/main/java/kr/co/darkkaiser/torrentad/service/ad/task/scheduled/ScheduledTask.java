package kr.co.darkkaiser.torrentad.service.ad.task.scheduled;

import kr.co.darkkaiser.torrentad.service.ad.task.Task;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public interface ScheduledTask extends Task {
	
	ScheduledTask setWebSite(WebSite site);

	void setBoardName(String name);

	void setLatestDownloadBoardItemIdentifier(long identifier);

	void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords);

	TaskResult run(WebSiteHandler handler);

}
