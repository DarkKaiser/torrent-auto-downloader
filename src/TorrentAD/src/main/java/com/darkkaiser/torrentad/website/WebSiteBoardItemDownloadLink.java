package com.darkkaiser.torrentad.website;

public interface WebSiteBoardItemDownloadLink {

	String getLink();

	String getFileName();

	boolean isDownloadable();

	void setDownloadable(final boolean flag);

	boolean isDownloadCompleted();

	void setDownloadCompleted(final boolean flag);

}
