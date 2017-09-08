package com.darkkaiser.torrentad.website;

import com.darkkaiser.torrentad.util.Tuple;

import java.util.Comparator;
import java.util.Iterator;

public interface WebSiteHandler {

	/**
	 * 검색하려는 게시판의 게시물을 조회한다.
	 * 
	 * @param board 검색하려는 게시판
	 * @param loadNow 검색하려는 게시판의 게시물을 다시 조회할지의 여부, false일 경우 기존에 읽어들인 데이터가 있을 경우 기존 데이터를 반환한다.
	 * @param comparator 검색된 게시물 정렬자
	 */
	Iterator<WebSiteBoardItem> list(final WebSiteBoard board, final boolean loadNow, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException;

	/**
	 * 검색컨텍스트에 등록된 게시판의 게시물을 조회하고, 등록된 검색조건과 일치하는 게시물을 모두 검색한다.
	 * 
	 * @param searchContext 검색컨텍스트
	 * @param loadNow 등록된 게시판의 게시물을 다시 조회할지의 여부, false일 경우 기존에 읽어들인 데이터가 있을 경우 기존 데이터를 반환한다.
	 * @param comparator 검색된 게시물 정렬자
	 */
	Iterator<WebSiteBoardItem> listAndFilter(final WebSiteSearchContext searchContext, final boolean loadNow, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException;

	/**
	 * 검색하려는 게시판에서 해당 키워드를 포함하는 게시물을 항상 다시 검색한다.
	 * 
	 * @param board 검색하려는 게시판
	 * @param keyword 검색 키워드
	 * @param comparator 검색된 게시물 정렬자
	 */
	Tuple<String/* 검색기록 Identifier */, Iterator<WebSiteBoardItem>/* 검색결과목록 */> search(final WebSiteBoard board, final String keyword, final Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException;

	/**
	 * 해당 ID에 해당하는 이전 검색정보 및 결과데이터를 찾아서 반환한다.
	 */
	WebSiteSearchResultData getSearchResultData(final String identifier);

	/**
	 * 해당 게시물의 첨부파일에 대한 다운로드 링크를 읽어들인다.
	 */
	boolean loadDownloadLink(final WebSiteBoardItem boardItem) throws NoPermissionException;

	/**
	 * 검색컨텍스트에 등록된 첨부파일 검색조건과 일치하는 첨부파일을 모두 다운로드한다.
	 */
	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download(final WebSiteBoardItem boardItem, final WebSiteSearchContext searchContext) throws NoPermissionException;

	/**
	 * 해당 인덱스에 해당하는 다운로드 링크 첨부파일을 다운로드한다.
	 */
	Tuple<Integer/* 다운로드시도횟수 */, Integer/* 다운로드성공횟수 */> download(final WebSiteBoardItem boardItem, final long downloadLinkIndex) throws NoPermissionException;
	
}
