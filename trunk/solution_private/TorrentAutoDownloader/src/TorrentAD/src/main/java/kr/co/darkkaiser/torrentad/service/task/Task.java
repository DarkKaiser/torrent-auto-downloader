package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public interface Task {

	TaskResult run(WebSiteHandler handler) throws Exception;

	// @@@@@
	String getBoardName();

	// @@@@@
	void setBoardName(String boardName);

	boolean isValid();

}
