package kr.co.darkkaiser.torrentad.service.task;

import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeyword;

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

	@Override
	public void setBoardName(String name) throws Exception {
		this.searchContext.setBoardName(name);
	}

	@Override
	public void add(WebSiteSearchKeyword searchKeyword) {
		// @@@@@
	}
	
	@Override
	public void validate() {
		if (this.taskType == null) {
			throw new NullPointerException("taskType");
		}
		if (this.site == null) {
			throw new NullPointerException("site");
		}
		if (this.searchContext == null) {
			throw new NullPointerException("searchContext");
		}

		this.searchContext.validate();
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractTask.class.getSimpleName())
				.append("{")
				.append("taskType:").append(this.taskType)
				.append(", site:").append(this.site)
				.append(", searchContext:").append(this.searchContext)
				.append("}")
				.toString();
	}
	
}
