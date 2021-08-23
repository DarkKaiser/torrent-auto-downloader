package com.darkkaiser.torrentad.website.impl.torrentmap;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.AllArgsConstructor;
import org.jsoup.helper.StringUtil;

@AllArgsConstructor
public enum TorrentMapBoard implements WebSiteBoard {

	/* 영화 */
	MOVIE_NEW			    ("newmovie", 			    "m01", 	"영화 > 최신영화", 		    TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=movie_new", 	true),

	/* 한국TV */
	KOR_DRAMA_ON		    ("kordramaon", 			    "k01", 	"한국TV > 드라마", 	        TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=kr_drama",		false),
	KOR_DRAMA_OVER		    ("kordramaover", 		    "k02", 	"한국TV > 드라마완결", 	    TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=kr_drama1",	true),
	KOR_ENTERTAINMENT_ON	("korentertainmenton",	    "k03", 	"한국TV > 예능/오락", 	    TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=kr_ent",	    false),
	KOR_ENTERTAINMENT_OVER	("korentertainmentover",	"k04", 	"한국TV > 예능/다큐완결", 	TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=kr_ent1",	    true),
	KOR_REFINEMENT	        ("korrefinement",	        "k05", 	"한국TV > 시사/교양", 	    TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=kr_daq",	    false),

	/* 외국TV */
	FOREIGN_DRAMA_ON	    ("foreigndramaon", 		    "f01", 	"외국TV > 드라마", 	        TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=eng_drama",	false),
	FOREIGN_DRAMA_OVER	    ("foreigndramaover",	    "f02", 	"외국TV > 드라마완결", 	    TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=eng_drama1",	true),

	/* 동영상 */
	VIDEO_ANI			    ("videoani", 			    "a01", 	"동영상 > 애니메이션", 		TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=ani",		    true),
	VIDEO_ETC			    ("videoetc", 			    "a02", 	"동영상 > 기타영상", 		TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=mv",		    true),

	/* 기타   */
	ETC_GAME				("etcgame", 			    "e01", 	"기타 > 게임", 				TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=game",		    true),
	ETC_UTIL				("etcutil", 			    "e02", 	"기타 > 유틸", 				TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=util",		    true),
	ETC_LECTURE				("etclecture", 			    "e03", 	"기타 > 도서/강좌", 			TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=lecture",		false),
	ETC_CHILDREN			("etcchildren", 		    "e04", 	"기타 > 어린이", 			TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=children",		false),
	ETC_ETC				    ("etcetc", 		    	    "e05", 	"기타 > 기타", 				TorrentMap.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=etc",		    false);

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

	public static TorrentMapBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TorrentMapBoard board : TorrentMapBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TorrentMapBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
