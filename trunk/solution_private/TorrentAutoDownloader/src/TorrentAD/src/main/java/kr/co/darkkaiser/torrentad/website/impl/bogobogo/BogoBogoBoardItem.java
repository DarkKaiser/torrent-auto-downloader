package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.text.ParseException;

import kr.co.darkkaiser.torrentad.website.board.AbstractWebSiteBoardItem;

public class BogoBogoBoardItem extends AbstractWebSiteBoardItem {

	// 게시물 상세페이지 URL
	private String detailPageURL;

	// @@@@@
	// 다운로드 항목 리스트 : id, val, val2, val3, val4, filename, filetype

	public BogoBogoBoardItem(BogoBogoBoard board, long identifier, String title, String registDateString) throws ParseException {
		super(board, identifier, title, registDateString);
	}

	public String getDetailPageURL() {
		return this.detailPageURL;
	}

	public void setDetailPageURL(String url) {
		this.detailPageURL = url;
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
