package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public interface Task {

	TaskType getTaskType();
	
	String getTaskId();

	void setBoardName(String name) throws Exception;

	void setLatestDownloadBoardItemIdentifier(long identifier) throws Exception;

	void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords) throws Exception;

	TaskResult run(WebSiteHandler handler) throws Exception;

	void validate();

	boolean isValid();

}
