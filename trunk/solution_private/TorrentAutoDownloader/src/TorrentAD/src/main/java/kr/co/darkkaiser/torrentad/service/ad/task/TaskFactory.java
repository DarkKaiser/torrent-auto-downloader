package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.scheduled.once.OnceScheduledTaskImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.scheduled.periodic.PeriodicScheduledTaskImpl;
import kr.co.darkkaiser.torrentad.website.WebSite;

public final class TaskFactory {

	// @@@@@ site가 없는것은???
	public static Task createTask(TaskType type, String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry, WebSite site) {
		if (type == TaskType.ONCE_SCHEDULED) {
			return new OnceScheduledTaskImpl(taskId, taskDescription, taskMetadataRegistry, site);
		} else if (type == TaskType.PERIODIC_SCHEDULED) {
			return new PeriodicScheduledTaskImpl(taskId, taskDescription, taskMetadataRegistry, site);
		} else if (type == TaskType.IMMEDIATELY) {
			return new ImmediatelyTaskImpl(taskId, taskDescription, taskMetadataRegistry);
		}

		throw new UnsupportedTaskException(String.format("구현되지 않은 Task 타입(%s)입니다.", type));
	}

	private TaskFactory() {
	}
	
}
