package kr.co.darkkaiser.torrentad.website;

import kr.co.darkkaiser.torrentad.website.board.WebSiteBoardItemIterator;

public interface WebSiteHandler {

	void login(WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	boolean isLogin();

	//@@@@@
	WebSiteBoardItemIterator search(WebSiteSearchContext taskContext);

}
