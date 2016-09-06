package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeyword;

public interface Task {
	
	TaskType getTaskType();

	void setBoardName(String name) throws Exception;

	void add(WebSiteSearchKeyword searchKeyword);

	TaskResult run(WebSiteHandler handler) throws Exception;

	void validate();
	
	boolean isValid();

}
