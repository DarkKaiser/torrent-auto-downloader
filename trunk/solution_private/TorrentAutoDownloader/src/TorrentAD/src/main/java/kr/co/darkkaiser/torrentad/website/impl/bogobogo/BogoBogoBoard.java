package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public enum BogoBogoBoard implements WebSiteBoard {

	MOVIE_NEW			(1, "newmovie", 			"영화 > 최신외국영화", 				BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=newmovie", 		true),
	MOVIE_KOR			(2, "kormovie", 			"영화 > 한국영화", 					BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kormovie",		true),
	MOVIE_HD			(3, "hdmovie", 				"영화 > DVD고화질영화", 			BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=hdmovie",		true),
	KOR_DRAMA_ON		(4, "kordramaon", 			"한국TV프로그램 > 드라마(방영중)", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kdramaon",		true),
	KOR_ENTERTAINMENT	(5, "korentertainment",		"한국TV프로그램 > 쇼/오락/스포츠", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=kentertain",	true),
	FOREIGN_DRAMA_ON	(6, "foreigndramaon", 		"외국TV프로그램 > 드라마(방영중)", 	BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=fdramaon",		true),
	ANI_ON				(7, "anion", 				"애니메이션 > 방영중", 				BogoBogo.BASE_URL_WITH_DEFAULT_PATH + "/board.php?board=anion",			false);

	private int id;
	private String name;
	private String description;
	private String url;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	private boolean hasCategory;

	private BogoBogoBoard(int id, String name, String description, String url, boolean hasCategory) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.url = url;
		this.hasCategory = hasCategory;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
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
	 * 게시판 목록의 등록일자 포맷을 반환합니다.
	 */
	@Override
	public String getDefaultRegistDateFormatString() {
		return "yy-MM-dd";
	}

	/**
	 * 게시판에서 읽어들일 페이지 수, 읽어들인 페이수에 해당하는 게시물을 이용하여 검색 작업을 수행한다.
	 */
	@Override
	public int getDefaultLoadPageCount() {
		return 5;
	}

	public boolean hasCategory() {
		return this.hasCategory;
	}

	public static BogoBogoBoard fromString(String name) {
		if (name == null)
			throw new NullPointerException("name");
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (BogoBogoBoard board : BogoBogoBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", BogoBogoBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%d:%s)", getDescription(), getId(), getName());
	}

}
