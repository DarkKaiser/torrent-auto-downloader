package kr.co.darkkaiser.torrentad.task;

import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

// @@@@ callable 인터페이스를 상속받지 않도록???
public interface Task {

	// @@@@@
	public Integer execute(WebSiteHandler l) throws Exception;


}
