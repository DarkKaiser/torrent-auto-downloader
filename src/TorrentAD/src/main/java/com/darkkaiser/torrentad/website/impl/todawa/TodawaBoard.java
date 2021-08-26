package com.darkkaiser.torrentad.website.impl.todawa;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jsoup.internal.StringUtil;

@Getter
@AllArgsConstructor
public enum TodawaBoard implements WebSiteBoard {

	/* 영화 */
	MOVIE		    ("movie", 	"movie", 		"영화", 		"/list-movie.html", 		false),

	/* 드라마 */
	DRAMA	    	("drama", 	"drama", 		"드라마", 	"/list-drama.html",		false),

	/* 예능 */
	ENTERTAINMENT	("ent", 		"ent", 		"예능", 		"/list-ent.html",			false),

	/* TV프로 */
	TV	    		("tv", 		"tv", 		"TV프로", 	"/list-tv.html",			false),

	/* 애니메이션 */
	ANIMATION		("animation", "animation",	"애니메이션", 	"/list-animation.html",	false),

	/* 음악 */
	MUSIC			("music", 	"music",		"음악", 		"/list-music.html",		false);

	private final String name;
	private final String code;
	private final String description;
	private final String path;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	@Accessors(fluent = true)
	private boolean hasCategory;

	public static TodawaBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TodawaBoard board : TodawaBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TodawaBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
