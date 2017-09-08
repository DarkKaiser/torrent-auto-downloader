package com.darkkaiser.torrentad.service.ad.task;

import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class AbstractTask implements Task {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTask.class);

	protected final TaskType taskType;
	
	protected final String taskId;
	protected final String taskDescription;
	
	protected final MetadataRepository metadataRepository;
	
	protected AbstractTask(final TaskType taskType, final String taskId, final String taskDescription, final MetadataRepository metadataRepository) {
		Objects.requireNonNull(taskType, "taskType");

		if (StringUtil.isBlank(taskId) == true)
			throw new IllegalArgumentException("taskId는 빈 문자열을 허용하지 않습니다.");

		Objects.requireNonNull(metadataRepository, "metadataRepository");

		this.taskId = taskId;
		this.taskType = taskType;
		this.metadataRepository = metadataRepository;

		if (StringUtil.isBlank(taskDescription) == false) {
			this.taskDescription = taskDescription;
		} else {
			this.taskDescription = taskId;
		}
	}

	@Override
	public TaskType getTaskType() {
		return this.taskType;
	}
	
	@Override
	public String getTaskId() {
		return this.taskId;
	}
	
	@Override
	public String getTaskDescription() {
		return this.taskDescription;
	}

	@Override
	public MetadataRepository getMetadataRepository() {
		return this.metadataRepository;
	}

	@Override
	public void validate() {
        Objects.requireNonNull(this.taskType, "taskType");

        if (StringUtil.isBlank(this.taskId) == true)
			throw new IllegalArgumentException("taskId는 빈 문자열을 허용하지 않습니다.");

        Objects.requireNonNull(this.metadataRepository, "metadataRepository");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (final Exception e) {
			logger.debug(null, e);
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return AbstractTask.class.getSimpleName() +
				"{" +
				"taskType:" + this.taskType +
				", taskId:" + this.taskId +
				", taskDescription:" + this.taskDescription +
				"}";
	}
	
}
