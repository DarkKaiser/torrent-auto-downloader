package kr.co.darkkaiser.torrentad.service.task.once;

import kr.co.darkkaiser.torrentad.service.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.task.TaskType;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class OnceTaskImpl extends AbstractTask implements OnceTask {
	
	public OnceTaskImpl(WebSite site) {
		super(TaskType.ONCE, site);
	}

	@Override
	public TaskResult run(WebSiteHandler handler) throws Exception {
		throw new UnsupportedOperationException("Not implemented, yet");
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public boolean isValid() {
		return super.isValid();
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
