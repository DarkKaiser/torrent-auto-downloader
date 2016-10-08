package kr.co.darkkaiser.torrentad.service.ad.task;

public interface Task {

	TaskType getTaskType();

	String getTaskId();

	String getTaskDescription();

	TaskMetadataRegistry getTaskMetadataRegistry();

	void validate();

	boolean isValid();

}
