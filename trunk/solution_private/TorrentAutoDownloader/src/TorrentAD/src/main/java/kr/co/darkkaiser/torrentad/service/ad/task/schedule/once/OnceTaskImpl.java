package kr.co.darkkaiser.torrentad.service.ad.task.schedule.once;

import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class OnceTaskImpl extends AbstractTask implements OnceTask {

	public OnceTaskImpl(String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry, WebSite site) {
		super(TaskType.ONCE, taskId, taskDescription, taskMetadataRegistry, site);
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
				.append(OnceTaskImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
