package com.darkkaiser.torrentad.website;

public interface WebSiteBoardItemDownloadLink {

	boolean isDownloadable();

	void setDownloadable(final boolean flag);

	boolean isDownloadCompleted();

	void setDownloadCompleted(final boolean flag);

}
