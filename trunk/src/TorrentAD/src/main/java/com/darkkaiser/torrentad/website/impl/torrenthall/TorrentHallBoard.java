package com.darkkaiser.torrentad.website.impl.torrenthall;

import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.jsoup.helper.StringUtil;

public enum TorrentHallBoard implements WebSiteBoard {

	/* 영화 */
	MOVIE_NEW			    ("movie", 		"m01", 	"영화", 			"/board/torrent_movie", 	false),

	/* 방송 */
	KOR_DRAMA_ON		    ("drama", 		"k01", 	"방송 > 드라마", 	"/board/torrent_drama",	false),
	KOR_ENTERTAINMENT_ON	("entertainment",	"k03", 	"방송 > 예능/오락", 	"/board/torrent_ent",		false),
	KOR_REFINEMENT	        ("refinement",	"k05", 	"방송 > 시사/교양", 	"/board/torrent_social",	false),

	/* 애니메이션 */
	VIDEO_ANI			    ("videoani", 		"a01", 	"애니메이션", 	 	"/board/torrent_ani",		false);

	private final String name;
	private final String code;
	private final String description;
	private final String path;

	// 게시물 목록에서 카테고리 정보를 가지고 있는지의 여부
	private boolean hasCategory;

	TorrentHallBoard(final String name, final String code, final String description, final String path, final boolean hasCategory) {
		this.name = name;
		this.code = code;
		this.description = description;
		this.path = path;
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
	public String getPath() {
		return this.path;
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

	public static TorrentHallBoard fromString(final String name) {
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");

		for (final TorrentHallBoard board : TorrentHallBoard.values()) {
			if (name.equals(board.getName()) == true)
				return board;
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", TorrentHallBoard.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return String.format("%s(%s:%s)", getDescription(), getCode(), getName());
	}

}
