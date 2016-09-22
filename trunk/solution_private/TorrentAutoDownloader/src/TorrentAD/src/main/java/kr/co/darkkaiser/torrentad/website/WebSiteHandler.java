package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

import kr.co.darkkaiser.torrentad.util.Tuple;

public interface WebSiteHandler {

	void login(WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	Iterator<WebSiteBoardItem> search(WebSiteSearchContext searchContext) throws FailedLoadBoardItemsException;

	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download(WebSiteSearchContext searchContext, WebSiteBoardItem boardItem) throws Exception;

	boolean isLogin();

}
