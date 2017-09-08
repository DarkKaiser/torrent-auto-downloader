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

	public AbstractScheduledTask(final TaskType taskType, final String taskId, final String taskDescription, final MetadataRepository metadataRepository) {
		super(taskType, taskId, taskDescription, metadataRepository);
	}

	@Override
	public ScheduledTask setWebSite(final WebSite site) {
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
	public void setBoardName(final String name) {
		this.searchContext.setBoardName(name);
	}

	@Override
	public void setLatestDownloadBoardItemIdentifier(final long identifier) {
		this.searchContext.setLatestDownloadBoardItemIdentifier(identifier);
	}

	@Override
	public void addSearchKeywords(final WebSiteSearchKeywordsType type, final WebSiteSearchKeywords searchKeywords) {
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
		return AbstractScheduledTask.class.getSimpleName() +
				"{" +
				"site:" + this.site +
				", searchContext:" + this.searchContext +
				"}, " +
				super.toString();
	}
	
}
