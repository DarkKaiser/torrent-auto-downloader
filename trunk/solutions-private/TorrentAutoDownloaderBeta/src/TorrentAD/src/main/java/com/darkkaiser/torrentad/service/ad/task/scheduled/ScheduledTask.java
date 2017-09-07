package com.darkkaiser.torrentad.service.ad.task.scheduled;

import com.darkkaiser.torrentad.service.ad.task.Task;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.website.WebSiteHandler;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;
import com.darkkaiser.torrentad.website.WebSite;

public interface ScheduledTask extends Task {
	
	ScheduledTask setWebSite(WebSite site);

	void setBoardName(String name);

	void setLatestDownloadBoardItemIdentifier(long identifier);

	void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords);

	TaskResult run(WebSiteHandler handler);

}
