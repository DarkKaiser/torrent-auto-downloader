package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

public enum BogoBogoBoard {

	MOVIE_NEW("newmovie", "영화 > 최신외국영화", BogoBogo.BASE_URL + "/cdsb/board.php?board=newmovie"),
	MOVIE_KOR("kormovie", "영화 > 한국영화", BogoBogo.BASE_URL + "/cdsb/board.php?board=kormovie"),
	MOVIE_HD("hdmovie", "영화 > DVD고화질영화", BogoBogo.BASE_URL + "/cdsb/board.php?board=hdmovie"),
	KOR_DRAMA_ON("kordramaon", "한국TV프로그램 > 드라마(방영중)", BogoBogo.BASE_URL + "/cdsb/board.php?board=kdramaon"),
	KOR_ENTERTAINMENT("korentertainment", "한국TV프로그램 > 쇼/오락/스포츠", BogoBogo.BASE_URL + "/cdsb/board.php?board=kentertain"),
	ANI_ON("anion", "애니메이션 > 방영중", BogoBogo.BASE_URL + "/cdsb/board.php?board=kentertain");

	private String name;
	private String description;
	private String url;

	private BogoBogoBoard(String name, String description, String url) {
		this.name = name;
		this.description = description;
		this.url = url;
	}
	
	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}
	
	public String getUrl() {
		return this.url;
	}

	public static BogoBogoBoard fromString(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (StringUtil.isBlank(name) == true) {
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");
		}

		if (name.equals(MOVIE_NEW.getName()) == true) {
			return MOVIE_NEW;
		} else if (name.equals(MOVIE_KOR.getName()) == true) {
			return MOVIE_KOR;
		} else if (name.equals(MOVIE_HD.getName()) == true) {
			return MOVIE_HD;
		} else if (name.equals(KOR_DRAMA_ON.getName()) == true) {
			return KOR_DRAMA_ON;
		} else if (name.equals(KOR_ENTERTAINMENT.getName()) == true) {
			return KOR_ENTERTAINMENT;
		} else if (name.equals(ANI_ON.getName()) == true) {
			return ANI_ON;
		}

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", BogoBogoBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getDescription(), getName());
	}

}
