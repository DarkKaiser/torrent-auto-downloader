package kr.co.darkkaiser.torrentad.service.ad.task.periodic;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.util.Tuple;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class PeriodicTaskImpl extends AbstractTask implements PeriodicTask {

	private static final Logger logger = LoggerFactory.getLogger(PeriodicTaskImpl.class);

	public PeriodicTaskImpl(WebSite site) {
		super(TaskType.PERIODIC, site);
	}

	@Override
	public TaskResult run(WebSiteHandler handler) throws Exception {
		if (handler == null) {
			throw new NullPointerException("handler");
		}

		validate();

		try {
			Iterator<WebSiteBoardItem> iterator = handler.search(this.searchContext);
			
			if (iterator.hasNext() == false) {
				logger.debug(String.format("검색 결과 게시물이 0건 입니다."));
			} else {
				while (iterator.hasNext()) {
					WebSiteBoardItem boardItem = iterator.next();
					assert boardItem != null;

					Tuple<Integer, Integer> downloadCount = handler.download(this.searchContext, boardItem);
					logger.info(String.format("검색된 게시물('%s')의 첨부파일 다운로드 작업이 종료되었습니다.(다운로드시도갯수:%d 다운로드성공갯수:%d)", boardItem.getTitle(), downloadCount.first(), downloadCount.last()));

					// 환경설정파일에 게시물 식별자를 저장한다.
					long identifier = boardItem.getIdentifier();
					long latestDownloadIdentifier = this.searchContext.getLatestDownloadIdentifier();
					
					assert identifier != Constants.INVALID_DOWNLOAD_IDENTIFIER_VALUE;

					if (latestDownloadIdentifier == Constants.INVALID_DOWNLOAD_IDENTIFIER_VALUE || latestDownloadIdentifier < identifier) {
						this.searchContext.setLatestDownloadIdentifier(identifier);

						// @@@@@
						// 정보 저장, 저장시에 해당 키워드의 아이디가 필요
					}
				}
			}
		} catch (FailedLoadBoardItemsException e) {
			logger.error(null, e);
			return TaskResult.FAILED;
		} catch (Exception e) {
			logger.error(null, e);
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
		return new StringBuilder()
				.append(PeriodicTaskImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
