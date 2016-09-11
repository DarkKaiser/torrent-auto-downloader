package kr.co.darkkaiser.torrentad.service.ad.task;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public abstract class AbstractTask implements Task {

	protected final TaskType taskType;
	
	protected final String taskId;

	protected final WebSite site;

	protected final WebSiteSearchContext searchContext;

	protected AbstractTask(TaskType taskType, String taskId, WebSite site) {
		if (taskType == null) {
			throw new NullPointerException("taskType");
		}
		if (StringUtil.isBlank(taskId) == true) {
			throw new IllegalArgumentException("taskId는 빈 문자열을 허용하지 않습니다.");
		}
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.site = site;
		this.taskId = taskId;
		this.taskType = taskType;
		this.searchContext = this.site.createSearchContext();
	}

	@Override
	public TaskType getTaskType() {
		return this.taskType;
	}
	
	@Override
	public String getTaskId() {
		return this.taskId;
	}

	@Override
	public void setBoardName(String name) throws Exception {
		this.searchContext.setBoardName(name);
	}

	@Override
	public void setLatestDownloadBoardItemIdentifier(long identifier) throws Exception {
		this.searchContext.setLatestDownloadBoardItemIdentifier(identifier);
	}

	@Override
	public void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords) throws Exception {
		this.searchContext.addSearchKeywords(type, searchKeywords);
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
				.append(", taskId:").append(this.taskId)
				.append(", site:").append(this.site)
				.append(", searchContext:").append(this.searchContext)
				.append("}")
				.toString();
	}
	
}
