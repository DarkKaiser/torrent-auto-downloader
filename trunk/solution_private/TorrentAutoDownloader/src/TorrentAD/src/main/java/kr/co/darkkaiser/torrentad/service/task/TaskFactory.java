package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.service.task.once.OnceTaskImpl;
import kr.co.darkkaiser.torrentad.service.task.periodic.PeriodicTaskImpl;
import kr.co.darkkaiser.torrentad.website.WebSite;

public final class TaskFactory {

	public static  Task newInstance(TaskType type, WebSite site) {
		if (type == TaskType.ONCE) {
			return new OnceTaskImpl(site);
		} else if (type == TaskType.PERIODIC) {
			return new PeriodicTaskImpl(site);
		}

		throw new IllegalArgumentException(String.format("구현되지 않은 Task 타입(%s)입니다.", type));
	}
	
}
