package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import kr.co.darkkaiser.torrentad.website.board.AbstractWebSiteBoardItem;

public class BogoBogoBoardItem extends AbstractWebSiteBoardItem {

	// 게시물 상세페이지 URL
	private String detailPageURL;

	// 게시물 첨부파일의 다운로드 링크 목록
	private ArrayList<BogoBogoBoardItemDownloadLink> downloadLinks = new ArrayList<>();

	public BogoBogoBoardItem(BogoBogoBoard board, long identifier, String title, String registDateString, String detailPageURL) throws ParseException {
		super(board, identifier, title, registDateString);
		
		this.detailPageURL = detailPageURL;
	}

	public String getDetailPageURL() {
		return this.detailPageURL;
	}

	public void addDownloadLink(BogoBogoBoardItemDownloadLink downloadLink) {
		if (downloadLink == null) {
			throw new NullPointerException("downloadLink");
		}

		this.downloadLinks.add(downloadLink);
	}

	public BogoBogoBoardItemDownloadLink getDownloadLink(int i) {
		// @@@@@
		return this.downloadLinks.get(i);
	}

	public void clearDownloadLink() {
		this.downloadLinks.clear();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(BogoBogoBoardItem.class.getSimpleName())
				.append("{")
				.append("detailPageURL:").append(getDetailPageURL())
				.append(", downloadLinks:");

		boolean firstKeyword = true;
		Iterator<BogoBogoBoardItemDownloadLink> iterator = this.downloadLinks.iterator();
		while (iterator.hasNext()) {
			if (firstKeyword == false) {
				sb.append("|")
				  .append(iterator.next());
			} else {
				firstKeyword = false;
				sb.append(iterator.next());
			}
		}

		sb.append("}, ")
		  .append(super.toString());

		return sb.toString();
	}

}
