package kr.co.darkkaiser.torrentad.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class RegularTaskImpl extends AbstractTask implements RegularTask {

	public RegularTaskImpl() {
	}

	@Override
	public TaskResult execute(WebSiteHandler handler) throws Exception {
		// @@@@@
//		/* 반환 */ l.search(/* 검색정보 */);
//		/* 반환받은 정보를 이용해서 다운로드 */
//		/* 결과정보*/l.download(/*다운로드정보*/);
//		l.upload(/*결과정보*/);
		
		return TaskResult.OK;
	}
	
}
