package com.darkkaiser.torrentad.website.impl.torrentmi;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.helper.StringUtil;

@Getter
public final class TorrentMiBoardItemDownloadLinkImpl implements TorrentMiBoardItemDownloadLink {

	private final String link;
	private final String fileName;

	@Setter
	private boolean downloadable = true;
	@Setter
	private boolean downloadCompleted = false;

	public static TorrentMiBoardItemDownloadLink newInstance(final String link, final String fileName) {
		return new TorrentMiBoardItemDownloadLinkImpl(link, fileName);
	}

	private TorrentMiBoardItemDownloadLinkImpl(final String link, final String fileName) {
		if (StringUtil.isBlank(link) == true)
			throw new IllegalArgumentException("link는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(fileName) == true)
			throw new IllegalArgumentException("fileName는 빈 문자열을 허용하지 않습니다.");

		this.link = link;
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return  getFileName();
	}

}
