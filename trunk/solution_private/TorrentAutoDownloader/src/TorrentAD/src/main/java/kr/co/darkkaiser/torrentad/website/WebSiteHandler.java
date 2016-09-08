package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

import kr.co.darkkaiser.torrentad.website.board.WebSiteBoardItem;

public interface WebSiteHandler {

	void login(WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	Iterator<WebSiteBoardItem> search(WebSiteSearchContext taskContext) throws Exception;

	boolean isLogin();

}
