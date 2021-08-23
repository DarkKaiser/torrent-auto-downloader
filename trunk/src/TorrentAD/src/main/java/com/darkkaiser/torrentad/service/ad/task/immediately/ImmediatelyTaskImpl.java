package com.darkkaiser.torrentad.service.ad.task.immediately;

import com.darkkaiser.torrentad.service.ad.task.AbstractTask;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.service.ad.task.TaskType;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ImmediatelyTaskImpl extends AbstractTask implements ImmediatelyTask {

	protected ImmediatelyTaskAction action;

	public ImmediatelyTaskImpl(final String taskId, final String taskDescription, final MetadataRepository metadataRepository) {
		super(TaskType.IMMEDIATELY, taskId, taskDescription, metadataRepository);
	}
	
	@Override
	public ImmediatelyTask setAction(final ImmediatelyTaskAction action) {
		this.action = action;
		return this;
	}

	@Override
	public TaskResult run() {
		validate();

		try {
			if (this.action.call() == false)
				return TaskResult.FAILED;
		} catch (final Exception e) {
			log.error(null, e);
			return TaskResult.UNEXPECTED_EXCEPTION;
		}

		return TaskResult.OK;
	}

	@Override
	public void validate() {
		super.validate();

		Objects.requireNonNull(this.action, "action");

		this.action.validate();
	}

	@Override
	public String toString() {
		return ImmediatelyTaskImpl.class.getSimpleName() +
				"{" +
				"action:" + this.action +
				"}, " +
				super.toString();
	}

}
