package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

public final class DefaultBogoBogoBoardItemDownloadLink implements BogoBogoBoardItemDownloadLink {

	private final String id;
	private final String value1;
	private final String value2;
	private final String value3;
	private final String value4;
	private final String fileId;
	private final String fileName;
	
	private boolean downloadCompleted = false;

	public static BogoBogoBoardItemDownloadLink newInstance(String id, String value1, String value2, String value3, String value4, String fileId, String fileName) {
		return new DefaultBogoBogoBoardItemDownloadLink(id, value1, value2, value3, value4, fileId, fileName);
	}

	private DefaultBogoBogoBoardItemDownloadLink(String id, String value1, String value2, String value3, String value4, String fileId, String fileName) {
		if (StringUtil.isBlank(id) == true) {
			throw new IllegalArgumentException("id는 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(value1) == true) {
			throw new IllegalArgumentException("value1는 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(value2) == true) {
			throw new IllegalArgumentException("value2는 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(value3) == true) {
			throw new IllegalArgumentException("value3는 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(value4) == true) {
			throw new IllegalArgumentException("value4는 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(fileId) == true) {
			throw new IllegalArgumentException("fileId는 빈 문자열을 허용하지 않습니다.");
		}
		if (StringUtil.isBlank(fileName) == true) {
			throw new IllegalArgumentException("fileName는 빈 문자열을 허용하지 않습니다.");
		}
		
		this.id = id;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.fileId = fileId;
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
	public String getFileId() {
		return this.fileId;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public boolean isDownloadCompleted() {
		return this.downloadCompleted;
	}
	
	@Override
	public void setDownloadCompleted(boolean flag) {
		this.downloadCompleted = flag;
	}

	@Override
	public String toString() {
		return getFileName();
	}
	
}
