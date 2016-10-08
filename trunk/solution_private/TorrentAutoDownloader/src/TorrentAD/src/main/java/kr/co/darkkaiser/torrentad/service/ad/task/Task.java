package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public interface Task {

	TaskType getTaskType();

	String getTaskId();

	String getTaskDescription();

	TaskMetadataRegistry getTaskMetadataRegistry();

	TaskResult run(WebSiteHandler handler);

	void validate();

	boolean isValid();

}
