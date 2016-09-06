package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public interface Task {
	
	TaskType getTaskType();

	void setBoardName(String name) throws Exception;

	TaskResult run(WebSiteHandler handler) throws Exception;

	void validate();
	
	boolean isValid();

}
