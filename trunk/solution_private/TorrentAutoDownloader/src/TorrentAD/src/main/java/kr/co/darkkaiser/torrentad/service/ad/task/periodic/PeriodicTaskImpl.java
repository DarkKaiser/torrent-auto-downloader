package kr.co.darkkaiser.torrentad.service.ad.task.periodic;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
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

		TaskResult result = TaskResult.OK;
		
		try {
			Iterator<WebSiteBoardItem> iterator = handler.search(this.searchContext);
			while (iterator.hasNext()) {
				WebSiteBoardItem boardItem = iterator.next();
				assert boardItem != null;

				if (handler.download(this.searchContext, boardItem) == true) {
					// @@@@@
					// 정보 저장
				} else {
					// @@@@@
				}
			}
		} catch (FailedLoadBoardItemsException e) {
			// @@@@@
			logger.error(null, e);
			result = TaskResult.BOARD_ITEMS_LOAD_FAILED;
		} catch (Exception e) {
			// @@@@@
			logger.error("예외가 발생하였습니다.", e);
			result = TaskResult.UNEXPECTED_EXCEPTION;
		}

		return result;
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
