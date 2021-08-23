package com.darkkaiser.torrentad.website.impl.totoria;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.helper.StringUtil;

@Getter
public final class TotoriaBoardItemDownloadLinkImpl implements TotoriaBoardItemDownloadLink {

	private final String link;
	private final String fileName;

	@Setter
	private boolean downloadable = true;
	@Setter
	private boolean downloadCompleted = false;

	public static TotoriaBoardItemDownloadLink newInstance(final String link, final String fileName) {
		return new TotoriaBoardItemDownloadLinkImpl(link, fileName);
	}

	private TotoriaBoardItemDownloadLinkImpl(final String link, final String fileName) {
		if (StringUtil.isBlank(link) == true)
			throw new IllegalArgumentException("link는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(fileName) == true)
			throw new IllegalArgumentException("fileName은 빈 문자열을 허용하지 않습니다.");
		
		this.link = link;
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return  getFileName();
	}

}
