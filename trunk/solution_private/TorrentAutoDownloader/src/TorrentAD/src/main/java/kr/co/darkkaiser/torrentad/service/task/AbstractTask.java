package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;

// @@@@@
public abstract class AbstractTask implements Task {
	
	protected final TaskType taskType;

	protected final WebSite site;
	protected final WebSiteSearchContext searchContext;

	public AbstractTask(TaskType taskType, WebSite site) {
		if (taskType == null) {
			throw new NullPointerException("taskType");
		}
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.taskType = taskType;

		this.site = site;
		this.searchContext = this.site.createSearchContext();
	}
	
	@Override
	public TaskType getTaskType() {
		return this.taskType;
	}

	// @@@@@
	@Override
	public void setBoardName(String boardName) {
		this.searchContext.setBoardName(boardName);
	}

}
