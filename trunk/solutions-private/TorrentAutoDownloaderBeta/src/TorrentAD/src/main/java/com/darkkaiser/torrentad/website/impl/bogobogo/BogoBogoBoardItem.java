package com.darkkaiser.torrentad.website.impl.bogobogo;

import java.text.ParseException;

import org.jsoup.helper.StringUtil;

import com.darkkaiser.torrentad.website.AbstractWebSiteBoardItem;

public class BogoBogoBoardItem extends AbstractWebSiteBoardItem {

	// 게시물 상세페이지 URL
	private final String detailPageURL;

	public BogoBogoBoardItem(BogoBogoBoard board, long identifier, String title, String registDateString, String detailPageURL) throws ParseException {
		super(board, identifier, title, registDateString);

		if (StringUtil.isBlank(detailPageURL) == true)
			throw new IllegalArgumentException("detailPageURL은 빈 문자열을 허용하지 않습니다.");

		this.detailPageURL = detailPageURL;
	}

	public String getDetailPageURL() {
		return this.detailPageURL;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoBoardItem.class.getSimpleName())
				.append("{")
				.append("detailPageURL:").append(getDetailPageURL())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
