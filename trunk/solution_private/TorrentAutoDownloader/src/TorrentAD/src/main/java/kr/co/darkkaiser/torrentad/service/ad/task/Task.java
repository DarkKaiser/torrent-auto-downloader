package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public interface Task {

	TaskType getTaskType();
	
	String getTaskId();
	
	TaskMetadataRegistry getTaskMetadataRegistry();

	void setBoardName(String name);

	void setLatestDownloadBoardItemIdentifier(long identifier);

	void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords);

	TaskResult run(WebSiteHandler handler);

	void validate();

	boolean isValid();

}
