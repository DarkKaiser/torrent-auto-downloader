package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.text.ParseException;

import kr.co.darkkaiser.torrentad.website.board.AbstractWebSiteBoardItem;

public class BogoBogoBoardItem extends AbstractWebSiteBoardItem {
	
	// 상세페이지 URL
	// @@@@@ 전체URL형태로변환되야됨, 형태: board.php?board=newmovie&amp;bm=view&amp;no=28585&amp;category=&amp;auth=&amp;page=1&amp;search=&amp;keyword=&amp;recom=
	private String detailPageUrl;

	// @@@@@
	// 다운로드 : id, val, val2, val3, val4, filename, filetype

	public BogoBogoBoardItem(BogoBogoBoard board, int identifier, String title, String registDateString) throws ParseException {
		super(board, identifier, title, registDateString);
	}

	public String getDetailPageUrl() {
		// @@@@@
		return this.detailPageUrl;
	}

	public void setDetailViewUrl(String url) {
//		 @@@@@ 
		this.detailPageUrl = url;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoBoardItem.class.getSimpleName())
				.append("{")
				.append("detailPageUrl:").append(getDetailPageUrl())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
