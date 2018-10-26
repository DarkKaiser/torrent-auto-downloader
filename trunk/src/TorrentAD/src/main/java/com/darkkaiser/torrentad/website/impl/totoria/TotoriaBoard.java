package com.darkkaiser.torrentad.website.impl.totoria;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.jsoup.helper.StringUtil;

public enum TotoriaBoard implements WebSiteBoard {

	/* 영화 */
	MOVIE_NEW			("newmovie", 			"m01", 	"영화 > 최신영화", 			Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_movie_eng"),
	MOVIE_HD			("hdmovie", 			"m02", 	"영화 > 고화질영화", 		Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_movie_dvd"),

	/* 국내TV */
	KOR_DRAMA			("kordrama", 			"k01", 	"국내TV > 국내 드라마", 	Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_kortv_drama"),
	KOR_ENTERTAINMENT	("korentertainment",	"k02", 	"국내TV > 국내 예능프로", 	Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_kortv_ent"),
	KOR_SOCIAL			("korsocial",			"k03", 	"국내TV > 국내 시사/다큐", 	Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_kortv_social"),

	/* 외국TV */
	FOREIGN_DRAMA		("foreigndrama", 		"f01", 	"외국TV > 외국 드라마", 	Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_engtv_drama"),

	/* 애니메이션 */
	ANI					("ani", 				"a01", 	"동영상 > 애니", 			Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_video_ani"),

	/* 기타 */
	ETC_GAME			("etcgame", 			"e01", 	"기타 > 게임", 				Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_game"),
	ETC_UTIL			("etcutil", 			"e02", 	"기타 > 유틸리티", 			Totoria.BASE_URL_WITH_DEFAULT_PATH + "/board.php?bo_table=torrent_util");

	private final String name;
	private final String code;
	private final String description;
	private final String url;

	TotoriaBoard(final String name, final String code, final String description, final String url) {
		this.name = name;
		this.code = code;
		this.description = description;
		this.url = url;
	}
	
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
	public String getURL() {
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

	public static TotoriaBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TotoriaBoard board : TotoriaBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TotoriaBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
