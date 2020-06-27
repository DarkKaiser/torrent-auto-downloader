package com.darkkaiser.torrentad.website;

public interface WebSiteBoard {

	String getName();
	
	String getCode();

	String getDescription();

	String getPath();

	/**
	 * 게시판에서 읽어들일 페이지 수, 읽어들인 페이수에 해당하는 게시물을 이용하여 검색 작업을 수행한다.
	 */
	default int getDefaultLoadPageCount() {
		return 4;
	}

	/**
	 * 게시판 목록의 등록일자 포맷을 반환합니다.
	 */
	default String getDefaultRegistDateFormatString() {
		return "yyyy.MM.dd";
	}

}
