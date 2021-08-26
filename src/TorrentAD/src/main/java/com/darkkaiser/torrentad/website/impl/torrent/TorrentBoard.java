package com.darkkaiser.torrentad.website.impl.torrent;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jsoup.helper.StringUtil;

@Getter
@AllArgsConstructor
public enum TorrentBoard implements WebSiteBoard {

	/* 해외영화 */
	MOVIE_OV	    ("movieov", 		"movieov", 		"해외영화", 	"/v-1-10", 		true),

	/* 한국영화 */
	MOVIE_KO	    ("movieko", 		"movieko", 		"한국영화", 	"/v-1-11", 		true),

	/* 해외드라마 */
	DRAMA_OV	   	("dramaov", 		"dramaov", 		"해외드라마", 	"/v-2-12",		false),

	/* 한국드라마 */
	DRAMA_KO	   	("dramako", 		"dramako", 		"한국드라마", 	"/v-2-13",		false),

	/* 예능 */
	ENTERTAINMENT	("ent", 			"ent", 			"예능", 		"/v-4-16",		false),

	/* 다큐/교양 */
	DOCUMENTARY		("documentary", 	"documentary", 	"다큐/교양", 	"/v-4-17",		false),

	/* 애니메이션 */
	ANIMATION		("animation", 	"animation",		"애니메이션", 	"/v-5-22",		false),

	/* 음악 */
	MUSIC			("music", 		"music",			"음악", 		"/v-4-19",		false);

	private final String name;
	private final String code;
	private final String description;
	private final String path;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	@Accessors(fluent = true)
	private final boolean hasCategory;

	public static TorrentBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TorrentBoard board : TorrentBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TorrentBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
