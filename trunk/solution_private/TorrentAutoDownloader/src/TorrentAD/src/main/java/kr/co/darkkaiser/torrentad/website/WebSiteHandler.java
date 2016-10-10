package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

import kr.co.darkkaiser.torrentad.util.Tuple;

public interface WebSiteHandler {

	Iterator<WebSiteBoardItem> list(WebSiteBoard board, boolean loadAlways) throws FailedLoadBoardItemsException;

	Iterator<WebSiteBoardItem> search(WebSiteSearchContext searchContext, boolean loadAlways) throws FailedLoadBoardItemsException;

	// @@@@@
	Iterator<WebSiteBoardItem> search(WebSiteBoard board, String searchKeyword, boolean loadAlways) throws FailedLoadBoardItemsException;

	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download(WebSiteSearchContext searchContext, WebSiteBoardItem boardItem) throws Exception;

}
