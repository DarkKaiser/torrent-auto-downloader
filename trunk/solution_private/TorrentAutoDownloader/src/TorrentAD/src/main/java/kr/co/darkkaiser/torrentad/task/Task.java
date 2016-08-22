package kr.co.darkkaiser.torrentad.task;

import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.WebSite;

// @@@@ callable 인터페이스를 상속받지 않도록???
public interface Task {

	// @@@@@
	public Integer execute(WebSite<BogoBogoWebSite> l) throws Exception;


}
