package kr.co.darkkaiser.torrentad.service.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.data.WebSiteData;

public class PeriodicTaskImpl extends AbstractTask implements PeriodicTask {

	private static final Logger logger = LoggerFactory.getLogger(PeriodicTaskImpl.class);
	
	// @@@@@
	private WebSiteData taskData;
	
	public PeriodicTaskImpl() {
	}

	@Override
	public TaskResult run(WebSiteHandler handler) throws Exception {
		
		// @@@@@
//		/* 반환 */ l.search(/* 검색정보 */);
//		/* 반환받은 정보를 이용해서 다운로드 */
//		/* 결과정보*/l.download(/*다운로드정보*/);
//		l.upload(/*결과정보*/);
		System.out.println("############# run");
		
//		handler.search(taskData);
		
		return TaskResult.OK;
	}
	
}
