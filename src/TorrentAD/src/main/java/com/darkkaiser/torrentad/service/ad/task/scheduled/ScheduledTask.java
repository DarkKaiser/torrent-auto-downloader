package com.darkkaiser.torrentad.service.ad.task.scheduled;

import com.darkkaiser.torrentad.service.ad.task.Task;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteHandler;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public interface ScheduledTask extends Task {
	
	ScheduledTask setWebSite(final WebSite site);

	void setBoardName(final String name);

	void setLatestDownloadBoardItemIdentifier(final long identifier);

	void addSearchKeywords(final WebSiteSearchKeywordsType type, final WebSiteSearchKeywords searchKeywords);

	TaskResult run(final WebSiteHandler handler);

}
