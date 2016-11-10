package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;

public interface Task {

	TaskType getTaskType();

	String getTaskId();

	String getTaskDescription();

	MetadataRepository getMetadataRepository();

	void validate();

	boolean isValid();

}
