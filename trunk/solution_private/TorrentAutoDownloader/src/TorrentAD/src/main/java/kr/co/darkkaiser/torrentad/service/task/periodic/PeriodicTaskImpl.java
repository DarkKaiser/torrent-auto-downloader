package kr.co.darkkaiser.torrentad.service.task.periodic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.task.TaskType;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.board.WebSiteBoardItemIterator;

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

		WebSiteBoardItemIterator iterator = handler.search(this.searchContext);
//		/* 반환받은 정보를 이용해서 다운로드 */
//		/* 결과정보*/l.download(/*다운로드정보*/);
//		l.upload(/*결과정보*/);
		System.out.println("############# run");

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
