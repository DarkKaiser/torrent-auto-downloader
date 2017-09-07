package com.darkkaiser.torrentad.service.ad.task.scheduled.once;

import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.service.ad.task.TaskType;
import com.darkkaiser.torrentad.service.ad.task.scheduled.AbstractScheduledTask;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.WebSiteHandler;

public class OnceScheduledTaskImpl extends AbstractScheduledTask implements OnceScheduledTask {

	public OnceScheduledTaskImpl(String taskId, String taskDescription, MetadataRepository metadataRepository) {
		super(TaskType.ONCE_SCHEDULED, taskId, taskDescription, metadataRepository);
	}

	@Override
	public TaskResult run(WebSiteHandler handler) {
		throw new UnsupportedOperationException("Not implemented, yet");
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(OnceScheduledTaskImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
