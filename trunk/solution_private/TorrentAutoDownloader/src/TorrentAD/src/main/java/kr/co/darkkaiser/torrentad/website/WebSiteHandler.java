package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

public interface WebSiteHandler {

	void login(WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	Iterator<WebSiteBoardItem> search(WebSiteSearchContext searchContext) throws Exception;

	boolean isLogin();

}
