package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

public interface BogoBogoBoardItemDownloadLink {

	String getId();

	String getValue1();
	
	String getValue2();
	
	String getValue3();
	
	String getValue4();
	
	String getFileId();
	
	String getFileName();
	
	boolean isDownloadable();
	
	void setDownloadable(boolean flag);

	boolean isDownloadCompleted();
	
	void setDownloadCompleted(boolean flag);

}
