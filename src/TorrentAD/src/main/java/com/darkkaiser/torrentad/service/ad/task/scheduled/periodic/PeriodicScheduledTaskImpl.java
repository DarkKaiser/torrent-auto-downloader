package com.darkkaiser.torrentad.service.ad.task.scheduled.periodic;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.service.ad.task.TaskType;
import com.darkkaiser.torrentad.service.ad.task.scheduled.AbstractScheduledTask;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Objects;

@Slf4j
public class PeriodicScheduledTaskImpl extends AbstractScheduledTask implements PeriodicScheduledTask {

	public PeriodicScheduledTaskImpl(final String taskId, final String taskDescription, final MetadataRepository metadataRepository) {
		super(TaskType.PERIODIC_SCHEDULED, taskId, taskDescription, metadataRepository);
	}

	@Override
	public TaskResult run(final WebSiteHandler handler) {
		Objects.requireNonNull(handler, "handler");

		validate();

		try {
			Iterator<WebSiteBoardItem> iterator = handler.listAndFilter(this.searchContext, true, new WebSiteBoardItemComparatorIdentifierAsc());

			if (iterator.hasNext() == false) {
				log.debug("검색된 게시물이 0건 입니다.(Task:{})", getTaskDescription());
			} else {
				while (iterator.hasNext()) {
					WebSiteBoardItem boardItem = iterator.next();
					assert boardItem != null;

					Tuple<Integer, Integer> downloadCount = handler.download(boardItem, this.searchContext);

					log.info(String.format("검색된 게시물('%s')의 첨부파일 다운로드 작업이 종료되었습니다.(Task:%s, 다운로드시도갯수:%d, 다운로드성공갯수:%d)", boardItem.getTitle(), getTaskDescription(), downloadCount.first(), downloadCount.last()));

					// 첨부파일을 1건 이상 다운로드 받은 경우라면 게시물 식별자를 저장하도록 한다.
					if (downloadCount.last() > 0) {
						// 환경설정파일에 게시물 식별자를 저장한다.
						long identifier = boardItem.getIdentifier();
						long latestDownloadBoardItemIdentifier = this.searchContext.getLatestDownloadBoardItemIdentifier();

						assert identifier != WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE;

						if (latestDownloadBoardItemIdentifier == WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE || latestDownloadBoardItemIdentifier < identifier) {
							this.searchContext.setLatestDownloadBoardItemIdentifier(identifier);

							// 다운로드 받은 게시물 식별자를 저장한다.
							String key = String.format("%s.%s", getTaskId(), Constants.AD_SERVICE_MR_KEY_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER);
							getMetadataRepository().setLong(key, identifier);
						}
					}
				}
			}
		} catch (final LoadBoardItemsException e) {
			log.error("게시판 데이터를 로드하는 중에 예외가 발생하였습니다.", e);
			return TaskResult.FAILED;
		} catch (final Exception e) {
			log.error(null, e);
			return TaskResult.UNEXPECTED_EXCEPTION;
		}

		return TaskResult.OK;
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return PeriodicScheduledTaskImpl.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
