package com.darkkaiser.torrentad.website.impl.bogobogo;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.jsoup.helper.StringUtil;

public enum BogoBogoBoard implements WebSiteBoard {

	/* 영화 */
	MOVIE_NEW			("newmovie", 			"m01", 	"영화 > 최신외국영화", 		BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=newmovie", 		true),
	MOVIE_KOR			("kormovie", 			"m02", 	"영화 > 한국영화", 			BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kormovie",		true),
	MOVIE_HD			("hdmovie", 			"m03", 	"영화 > DVD고화질영화", 	    BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=hdmovie",		true),
	
	/* 한국TV */
	KOR_DRAMA_ON		("kordramaon", 			"k01", 	"한국TV > 드라마(방영중)", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kdramaon",		true),
	KOR_DRAMA_OVER		("kordramaover", 		"k02", 	"한국TV > 드라마(종방)", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kdramaover",	false),
	KOR_ENTERTAINMENT	("korentertainment",	"k03", 	"한국TV > 쇼/오락/스포츠", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kentertain",	true),

	/* 외국TV */
	FOREIGN_DRAMA_ON	("foreigndramaon", 		"f01", 	"외국TV > 드라마(방영중)", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=fdramaon",		true),
	FOREIGN_DRAMA_OVER	("foreigndramaover",	"f02", 	"외국TV > 드라마(종방)", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=fdramaover",	false),

	/* 애니메이션 */
	ANI_OVER			("aniover", 			"a01", 	"애니메이션 > 종방", 		BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=aniover",		false),
	ANI_ON				("anion", 				"a02", 	"애니메이션 > 방영중", 		BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=anion",			false),
	
	/* 유아, 아동용 */
	BABY				("baby", 				"b01", 	"유아, 아동용", 			    BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=baby",			false),
	
	/* 게임 */
	GAME				("game", 				"g01", 	"게임", 					    BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=torgame",		true),

	/* 만화책, 소설 */
	BOOK				("book", 				"h01", 	"만화책, 소설", 			    BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=torbook",		true),
	
	/* 유틸리티, 강좌 */
	UTIL				("util", 				"u01", 	"유틸리티, 강좌", 			BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=torutil",		true),
	
	/* 자막 나누기 */
	SMI					("smi", 				"s01", 	"자막 나누기", 				BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=smi",			true);

	private final String name;
	private final String code;
	private final String description;
	private final String url;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	private boolean hasCategory;

	BogoBogoBoard(final String name, final String code, final String description, final String url, final boolean hasCategory) {
		this.name = name;
		this.code = code;
		this.description = description;
		this.url = url;
		this.hasCategory = hasCategory;
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
		return "yy-MM-dd";
	}

	public boolean hasCategory() {
		return this.hasCategory;
	}

	public static BogoBogoBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final BogoBogoBoard board : BogoBogoBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", BogoBogoBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
