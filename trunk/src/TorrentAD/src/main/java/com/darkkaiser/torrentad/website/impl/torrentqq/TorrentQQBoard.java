package com.darkkaiser.torrentad.website.impl.torrentqq;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jsoup.helper.StringUtil;

@Getter
@AllArgsConstructor
public enum TorrentQQBoard implements WebSiteBoard {

	/* 영화 */
	MOVIE		    	("movie", 		"mov", 	"영화", 		"/torrent/mov.html", 	false),

	/* 방송 */
	BROADCASTING	    ("broadcasting", 	"med", 	"방송", 		"/torrent/med.html",	false),

	/* 애니메이션 */
	ANIMATION		    ("animation", 	"ani", 	"애니메이션", 	"/torrent/ani.html",	false);

	private final String name;
	private final String code;
	private final String description;
	private final String path;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	@Accessors(fluent = true)
	private boolean hasCategory;

	public static TorrentQQBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TorrentQQBoard board : TorrentQQBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TorrentQQBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
