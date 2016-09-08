package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public enum BogoBogoBoard implements WebSiteBoard {

	MOVIE_NEW			("newmovie", 			"영화 > 최신외국영화", 				BogoBogo.BASE_URL_WITH_PATH + "/board.php?board=newmovie"),
	MOVIE_KOR			("kormovie", 			"영화 > 한국영화", 					BogoBogo.BASE_URL_WITH_PATH + "/board.php?board=kormovie"),
	MOVIE_HD			("hdmovie", 			"영화 > DVD고화질영화", 			BogoBogo.BASE_URL_WITH_PATH + "/board.php?board=hdmovie"),
	KOR_DRAMA_ON		("kordramaon", 			"한국TV프로그램 > 드라마(방영중)", 	BogoBogo.BASE_URL_WITH_PATH + "/board.php?board=kdramaon"),
	KOR_ENTERTAINMENT	("korentertainment",	"한국TV프로그램 > 쇼/오락/스포츠", 	BogoBogo.BASE_URL_WITH_PATH + "/board.php?board=kentertain"),
	ANI_ON				("anion", 				"애니메이션 > 방영중", 				BogoBogo.BASE_URL_WITH_PATH + "/board.php?board=anion");

	private String name;
	private String description;
	private String url;
	
	private BogoBogoBoard(String name, String description, String url) {
		this.name = name;
		this.description = description;
		this.url = url;
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
	 * 게시판에서 읽어들일 페이지 수, 읽어들인 게시물을 이용하여 검색 작업을 수행한다.
	 */
	@Override
	public int getDefaultLoadPageCount() {
		return 5;
	}

	public static BogoBogoBoard fromString(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (StringUtil.isBlank(name) == true) {
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");
		}

		for (BogoBogoBoard board : BogoBogoBoard.values()) {
			if (name.equals(board.getName()) == true) {
				return board;
			}
	    }
		
		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", BogoBogoBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getDescription(), getName());
	}

}
