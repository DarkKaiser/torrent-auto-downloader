package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.scheduled.once.OnceScheduledTaskImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.scheduled.periodic.PeriodicScheduledTaskImpl;
import kr.co.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;

public final class TaskFactory {

	public static Task createTask(TaskType type, String taskId, String taskDescription, MetadataRepository metadataRepository) {
		if (type == TaskType.ONCE_SCHEDULED) {
			return new OnceScheduledTaskImpl(taskId, taskDescription, metadataRepository);
		} else if (type == TaskType.PERIODIC_SCHEDULED) {
			return new PeriodicScheduledTaskImpl(taskId, taskDescription, metadataRepository);
		} else if (type == TaskType.IMMEDIATELY) {
			return new ImmediatelyTaskImpl(taskId, taskDescription, metadataRepository);
		}

		throw new UnsupportedTaskException(String.format("구현되지 않은 Task 타입(%s)입니다.", type));
	}

	private TaskFactory() {
	}
	
}
