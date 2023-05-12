package com.darkkaiser.torrentad.service.ad.task;

import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;

import java.util.Objects;

@Slf4j
@Getter
public abstract class AbstractTask implements Task {

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
			log.debug(null, e);
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
