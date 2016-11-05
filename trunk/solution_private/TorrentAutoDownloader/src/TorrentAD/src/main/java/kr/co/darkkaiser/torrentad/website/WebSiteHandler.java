package kr.co.darkkaiser.torrentad.website;

import java.util.Comparator;
import java.util.Iterator;

import kr.co.darkkaiser.torrentad.util.Tuple;

public interface WebSiteHandler {

	/**
	 * 검색하려는 게시판의 게시물을 조회한다.
	 * 
	 * @param board 검색하려는 게시판
	 * @param loadNow 검색하려는 게시판의 게시물을 다시 조회할지의 여부, false일 경우 기존에 읽어들인 데이터가 있을 경우 기존 데이터를 반환한다.
	 * @param comparator 검색된 게시물 정렬자
	 */
	Iterator<WebSiteBoardItem> list(WebSiteBoard board, boolean loadNow, Comparator<? super WebSiteBoardItem> comparator) throws FailedLoadBoardItemsException;

	/**
	 * 검색컨텍스트에 등록된 게시판의 게시물을 조회하고, 등록된 검색조건과 일치하는 게시물을 모두 검색한다.
	 * 
	 * @param searchContext 검색컨텍스트
	 * @param loadNow 등록된 게시판의 게시물을 다시 조회할지의 여부, false일 경우 기존에 읽어들인 데이터가 있을 경우 기존 데이터를 반환한다.
	 * @param comparator 검색된 게시물 정렬자
	 */
	Iterator<WebSiteBoardItem> listAndSearch(WebSiteSearchContext searchContext, boolean loadNow, Comparator<? super WebSiteBoardItem> comparator) throws FailedLoadBoardItemsException;

	/**
	 * 검색하려는 게시판에서 해당 키워드를 포함하는 게시물을 항상 다시 검색한다.
	 * 
	 * @param board 검색하려는 게시판
	 * @param keyword 검색 키워드
	 * @param comparator 검색된 게시물 정렬자
	 */
	Iterator<WebSiteBoardItem> searchNow(WebSiteBoard board, String keyword, Comparator<? super WebSiteBoardItem> comparator) throws FailedLoadBoardItemsException;

	/**
	 * 검색컨텍스트에 등록된 첨부파일 검색조건과 일치하는 첨부파일을 모두 다운로드한다.
	 */
	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download(WebSiteBoardItem boardItem, WebSiteSearchContext searchContext) throws Exception;
	
	// @@@@@
	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download2(WebSiteBoardItem boardItem, long index) throws Exception;
	void download3(WebSiteBoardItem boardItem) throws Exception;
	
}
