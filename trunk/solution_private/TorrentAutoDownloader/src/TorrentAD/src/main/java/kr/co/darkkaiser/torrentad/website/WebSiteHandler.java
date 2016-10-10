package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

import kr.co.darkkaiser.torrentad.util.Tuple;

public interface WebSiteHandler {

	/**
	 * 해당 게시판의 게시물을 조회한다.
	 */
	Iterator<WebSiteBoardItem> list(WebSiteBoard board, boolean loadAlways) throws FailedLoadBoardItemsException;

	/**
	 * 검색컨텍스트에 등록된 게시판의 게시물을 조회하고, 등록된 검색조건과 일치하는 게시물을 모두 검색한다.
	 */
	Iterator<WebSiteBoardItem> listAndSearch(WebSiteSearchContext searchContext, boolean loadAlways) throws FailedLoadBoardItemsException;

	/**
	 * 해당 키워드를 포함하는 모든 게시판의 게시물을 검색한다.
	 */
	Iterator<WebSiteBoardItem> searchAll(String keyword) throws Exception;

	/**
	 * 검색컨텍스트에 등록된 첨부파일 검색조건과 일치하는 첨부파일을 모두 다운로드한다.
	 */
	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download(WebSiteSearchContext searchContext, WebSiteBoardItem boardItem) throws Exception;

}
