package com.darkkaiser.torrentad.website.impl.torrentmi;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.AllArgsConstructor;
import org.jsoup.helper.StringUtil;

@AllArgsConstructor
public enum TorrentMiBoard implements WebSiteBoard {

	MOVIE		    ("movie", 			"m01", 	"영화", 		    TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=tmovie", false),
	DRAMA		    ("drama", 			"d01", 	"드라마", 	    TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=tdrama",	false),
	ENTERTAINMENT   ("entertainment", 	"e01", 	"예능프로", 	    TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=tent",	false),
	TV	    	    ("tv", 			    "t01", 	"TV프로", 	    TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=tv",		false),
	ANI		        ("ani", 			"a01", 	"애니메이션", 	TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=tani",	false),
	MUSIC		    ("music",           "s01", 	"음악", 	        TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=tmusic",	false),
	UTIL		    ("util",            "u01", 	"유틸리티", 	    TorrentMi.BASE_URL_WITH_DEFAULT_PATH + "/list.php?b_id=util",	false);

	private final String name;
	private final String code;
	private final String description;
	private final String url;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	private boolean hasCategory;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public String getPath() {
		return this.url;
	}

	/**
	 * 게시판에서 읽어들일 페이지 수, 읽어들인 페이수에 해당하는 게시물을 이용하여 검색 작업을 수행한다.
	 */
	@Override
	public int getDefaultLoadPageCount() {
		return 4;
	}

	/**
	 * 게시판 목록의 등록일자 포맷을 반환합니다.
	 */
	@Override
	public String getDefaultRegistDateFormatString() {
		return "yyyy.MM.dd";
	}

	public boolean hasCategory() {
		return this.hasCategory;
	}

	public static TorrentMiBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TorrentMiBoard board : TorrentMiBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TorrentMiBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
