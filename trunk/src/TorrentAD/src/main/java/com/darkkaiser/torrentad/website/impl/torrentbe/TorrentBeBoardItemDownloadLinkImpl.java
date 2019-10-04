package com.darkkaiser.torrentad.website.impl.torrentbe;

import org.jsoup.helper.StringUtil;

public final class TorrentBeBoardItemDownloadLinkImpl implements TorrentBeBoardItemDownloadLink {

	private final String link;
	private final String fileName;

	private boolean downloadable = true;
	private boolean downloadCompleted = false;

	public static TorrentBeBoardItemDownloadLink newInstance(final String link, final String fileName) {
		return new TorrentBeBoardItemDownloadLinkImpl(link, fileName);
	}

	private TorrentBeBoardItemDownloadLinkImpl(final String link, final String fileName) {
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
