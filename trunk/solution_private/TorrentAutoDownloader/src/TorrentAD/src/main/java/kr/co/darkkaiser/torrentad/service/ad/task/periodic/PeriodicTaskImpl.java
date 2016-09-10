package kr.co.darkkaiser.torrentad.service.ad.task.periodic;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class PeriodicTaskImpl extends AbstractTask implements PeriodicTask {

	@SuppressWarnings("unused")
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

		// @@@@@
		////////////////////////////////////////////////////////////////////////////////////
		TaskResult result = TaskResult.OK;
		Iterator<WebSiteBoardItem> iterator = handler.search(this.searchContext);
		if (iterator == null) {
			
		}

		while (iterator.hasNext()) {
			WebSiteBoardItem boardItem = iterator.next();
			assert boardItem != null;
			
			if (handler.download(this.searchContext, boardItem) == true) {
				// 정보 저장
				// 검색할때도 키 이후의 정보만 검색되어야 함
			} else {
				
			}
		}
		////////////////////////////////////////////////////////////////////////////////////

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
