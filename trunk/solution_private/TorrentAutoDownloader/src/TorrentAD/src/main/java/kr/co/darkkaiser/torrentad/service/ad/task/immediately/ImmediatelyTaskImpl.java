package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;

public class ImmediatelyTaskImpl extends AbstractTask implements ImmediatelyTask {

	public ImmediatelyTaskImpl(String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry) {
		super(TaskType.IMMEDIATELY, taskId, taskDescription, taskMetadataRegistry);
	}

	@Override
	public TaskResult run() {
		// @@@@@
		return null;
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ImmediatelyTaskImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
