package kr.co.darkkaiser.torrentad.service.ad.task.scheduled;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteConstants;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public abstract class AbstractScheduledTask extends AbstractTask implements ScheduledTask {

	protected WebSite site;

	protected WebSiteSearchContext searchContext;

	public AbstractScheduledTask(TaskType taskType, String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry) {
		super(taskType, taskId, taskDescription, taskMetadataRegistry);
	}

	@Override
	public ScheduledTask setWebSite(WebSite site) {
		if (site == null)
			throw new NullPointerException("site");

		this.site = site;
		this.searchContext = this.site.createSearchContext();

		// 최근에 다운로드 받은 게시불 식별자를 구한다.
		String key = String.format("%s.%s", this.taskId, Constants.AD_SERVICE_TASK_METADATA_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER);
		setLatestDownloadBoardItemIdentifier(taskMetadataRegistry.getLong(key, WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE));
		
		return this;
	}

	@Override
	public void setBoardName(String name) {
		this.searchContext.setBoardName(name);
	}

	@Override
	public void setLatestDownloadBoardItemIdentifier(long identifier) {
		this.searchContext.setLatestDownloadBoardItemIdentifier(identifier);
	}

	@Override
	public void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords) {
		this.searchContext.addSearchKeywords(type, searchKeywords);
	}

	@Override
	public void validate() {
		if (this.site == null)
			throw new NullPointerException("site");
		if (this.searchContext == null)
			throw new NullPointerException("searchContext");

		this.searchContext.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractScheduledTask.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append(", searchContext:").append(this.searchContext)
				.append("}, ")
				.append(super.toString())
				.toString();
	}
	
}
