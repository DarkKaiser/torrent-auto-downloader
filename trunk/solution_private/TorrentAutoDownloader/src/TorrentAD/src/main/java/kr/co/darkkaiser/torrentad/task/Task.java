package kr.co.darkkaiser.torrentad.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public interface Task {

	TaskResult execute(WebSiteHandler handler) throws Exception;

}
