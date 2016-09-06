package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;

public interface Task {
	
	TaskType getTaskType();

	void setBoardName(String name) throws Exception;

	void addSearchKeywords(WebSiteSearchKeywords searchKeywords) throws Exception;

	TaskResult run(WebSiteHandler handler) throws Exception;

	void validate();

	boolean isValid();

}
