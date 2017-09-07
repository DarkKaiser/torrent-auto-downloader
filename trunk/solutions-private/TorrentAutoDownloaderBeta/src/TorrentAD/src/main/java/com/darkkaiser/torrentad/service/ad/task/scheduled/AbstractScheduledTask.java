package com.darkkaiser.torrentad.service.ad.task.scheduled;

import com.darkkaiser.torrentad.website.WebSiteConstants;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;
import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.service.ad.task.AbstractTask;
import com.darkkaiser.torrentad.service.ad.task.TaskType;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteSearchContext;

public abstract class AbstractScheduledTask extends AbstractTask implements ScheduledTask {

	protected WebSite site;

	protected WebSiteSearchContext searchContext;

	public AbstractScheduledTask(TaskType taskType, String taskId, String taskDescription, MetadataRepository metadataRepository) {
		super(taskType, taskId, taskDescription, metadataRepository);
	}

	@Override
	public ScheduledTask setWebSite(WebSite site) {
		if (site == null)
			throw new NullPointerException("site");

		this.site = site;
		this.searchContext = this.site.createSearchContext();

		// 최근에 다운로드 받은 게시물 식별자를 구한다.
		String key = String.format("%s.%s", this.taskId, Constants.AD_SERVICE_MR_KEY_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER);
		setLatestDownloadBoardItemIdentifier(metadataRepository.getLong(key, WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE));
		
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
