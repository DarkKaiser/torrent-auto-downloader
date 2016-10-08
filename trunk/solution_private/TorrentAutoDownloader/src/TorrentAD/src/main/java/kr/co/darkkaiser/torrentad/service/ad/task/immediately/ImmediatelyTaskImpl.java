package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.service.ad.task.scheduled.periodic.PeriodicScheduledTaskImpl;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class ImmediatelyTaskImpl extends AbstractTask implements ImmediatelyTask {

	private static final Logger logger = LoggerFactory.getLogger(PeriodicScheduledTaskImpl.class);

	public ImmediatelyTaskImpl(String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry, WebSite site) {
		super(TaskType.IMMEDIATELY, taskId, taskDescription, taskMetadataRegistry, site);
	}

	@Override
	public TaskResult run(WebSiteHandler handler) {
		// @@@@@
//		if (handler == null)
//			throw new NullPointerException("handler");
//
//		validate();
//
//		try {
//			Iterator<WebSiteBoardItem> iterator = handler.search(this.searchContext);
//
//			if (iterator.hasNext() == false) {
//				logger.debug("검색된 게시물이 0건 입니다.(Task:{})", getTaskDescription());
//			} else {
//				while (iterator.hasNext()) {
//					WebSiteBoardItem boardItem = iterator.next();
//					assert boardItem != null;
//
//					Tuple<Integer, Integer> downloadCount = handler.download(this.searchContext, boardItem);
//					logger.info(String.format("검색된 게시물('%s')의 첨부파일 다운로드 작업이 종료되었습니다.(Task:%s, 다운로드시도갯수:%d, 다운로드성공갯수:%d)", boardItem.getTitle(), getTaskDescription(), downloadCount.first(), downloadCount.last()));
//
//					// 환경설정파일에 게시물 식별자를 저장한다.
//					long identifier = boardItem.getIdentifier();
//					long latestDownloadBoardItemIdentifier = this.searchContext.getLatestDownloadBoardItemIdentifier();
//					
//					assert identifier != WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE;
//
//					if (latestDownloadBoardItemIdentifier == WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE || latestDownloadBoardItemIdentifier < identifier) {
//						this.searchContext.setLatestDownloadBoardItemIdentifier(identifier);
//
//						// 다운로드 받은 게시물 식별자를 저장한다.
//						String key = String.format("%s.%s", getTaskId(), Constants.AD_SERVICE_TASK_METADATA_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER);
//						getTaskMetadataRegistry().setLong(key, identifier);
//					}
//				}
//			}
//		} catch (FailedLoadBoardItemsException e) {
//			logger.error("게시판 데이터를 로드하는 중에 예외가 발생하였습니다.", e);
//			return TaskResult.FAILED;
//		} catch (Exception e) {
//			logger.error(null, e);
//			return TaskResult.UNEXPECTED_EXCEPTION;
//		}
//
//		return TaskResult.OK;
		return null;
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ImmediatelyTaskImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
