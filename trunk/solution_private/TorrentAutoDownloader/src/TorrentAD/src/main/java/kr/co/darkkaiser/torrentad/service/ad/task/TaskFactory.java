package kr.co.darkkaiser.torrentad.service.ad.task;

import kr.co.darkkaiser.torrentad.service.ad.task.once.OnceTaskImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.periodic.PeriodicTaskImpl;
import kr.co.darkkaiser.torrentad.website.WebSite;

public final class TaskFactory {

	public static Task createTask(TaskType type, String taskId, TaskMetadataRegistry taskMetadataRegistry, WebSite site) {
		if (type == TaskType.ONCE) {
			return new OnceTaskImpl(taskId, taskMetadataRegistry, site);
		} else if (type == TaskType.PERIODIC) {
			return new PeriodicTaskImpl(taskId, taskMetadataRegistry, site);
		}

		throw new UnsupportedTaskException(String.format("구현되지 않은 Task 타입(%s)입니다.", type));
	}
	
}
