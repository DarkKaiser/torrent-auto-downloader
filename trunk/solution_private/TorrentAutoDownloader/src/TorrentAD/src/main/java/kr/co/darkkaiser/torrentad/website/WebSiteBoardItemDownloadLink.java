package kr.co.darkkaiser.torrentad.website;

public interface WebSiteBoardItemDownloadLink {

	boolean isDownloadable();
	
	void setDownloadable(boolean flag);

	boolean isDownloadCompleted();
	
	void setDownloadCompleted(boolean flag);

}
