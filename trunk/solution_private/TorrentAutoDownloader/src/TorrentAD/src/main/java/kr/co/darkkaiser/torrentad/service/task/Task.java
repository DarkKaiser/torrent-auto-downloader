package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public interface Task {
	
	TaskType getTaskType();

	TaskResult run(WebSiteHandler handler) throws Exception;

	// @@@@@
	void setBoardName(String boardName);

	void validate();
	
	boolean isValid();

}
