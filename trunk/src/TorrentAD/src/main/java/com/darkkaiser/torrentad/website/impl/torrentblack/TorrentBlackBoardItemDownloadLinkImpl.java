package com.darkkaiser.torrentad.website.impl.torrentblack;

import org.jsoup.helper.StringUtil;

public final class TorrentBlackBoardItemDownloadLinkImpl implements TorrentBlackBoardItemDownloadLink {

	private final String link;
	private final String fileName;

	private boolean downloadable = true;
	private boolean downloadCompleted = false;

	public static TorrentBlackBoardItemDownloadLink newInstance(final String link, final String fileName) {
		return new TorrentBlackBoardItemDownloadLinkImpl(link, fileName);
	}

	private TorrentBlackBoardItemDownloadLinkImpl(final String link, final String fileName) {
		if (StringUtil.isBlank(link) == true)
			throw new IllegalArgumentException("link는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(fileName) == true)
			throw new IllegalArgumentException("fileName는 빈 문자열을 허용하지 않습니다.");

		this.link = link;
		this.fileName = fileName;
	}

	@Override
	public String getLink() {
		return this.link;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}
	
	@Override
	public boolean isDownloadable() {
		return this.downloadable;
	}
	
	@Override
	public void setDownloadable(final boolean flag) {
		this.downloadable = flag;
	}
	
	@Override
	public boolean isDownloadCompleted() {
		return this.downloadCompleted;
	}
	
	@Override
	public void setDownloadCompleted(final boolean flag) {
		this.downloadCompleted = flag;
	}

	@Override
	public String toString() {
		return  getFileName();
	}

}
