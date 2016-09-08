package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

public class DefaultBogoBogoBoardItemDownloadLink implements BogoBogoBoardItemDownloadLink {

	private final String id;

	private final String value1;
	private final String value2;
	private final String value3;
	private final String value4;

	private final String fileName;

	public static BogoBogoBoardItemDownloadLink newInstance(String id, String value1, String value2, String value3, String value4, String fileName) {
		return new DefaultBogoBogoBoardItemDownloadLink(id, value1, value2, value3, value4, fileName);
	}

	private DefaultBogoBogoBoardItemDownloadLink(String id, String value1, String value2, String value3, String value4, String fileName) {
		this.id = id;
		
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		
		this.fileName = fileName;
	}
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getValue1() {
		return this.value1;
	}

	@Override
	public String getValue2() {
		return this.value2;
	}

	@Override
	public String getValue3() {
		return this.value3;
	}

	@Override
	public String getValue4() {
		return this.value4;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return getFileName();
	}

}
