package com.darkkaiser.torrentad.website.impl.bogobogo;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.internal.StringUtil;

@Getter
public final class BogoBogoBoardItemDownloadLinkImpl implements BogoBogoBoardItemDownloadLink {

	private final String id;
	private final String value1;
	private final String value2;
	private final String value3;
	private final String value4;
	private final String fileId;
	private final String fileName;

	@Setter
	private boolean downloadable = true;
	@Setter
	private boolean downloadCompleted = false;

	public static BogoBogoBoardItemDownloadLink newInstance(final String id, final String value1, final String value2, final String value3, final String value4, final String fileId, final String fileName) {
		return new BogoBogoBoardItemDownloadLinkImpl(id, value1, value2, value3, value4, fileId, fileName);
	}

	private BogoBogoBoardItemDownloadLinkImpl(final String id, final String value1, final String value2, final String value3, final String value4, final String fileId, final String fileName) {
		if (StringUtil.isBlank(id) == true)
			throw new IllegalArgumentException("id는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(value1) == true)
			throw new IllegalArgumentException("value1는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(value3) == true)
			throw new IllegalArgumentException("value3는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(value4) == true)
			throw new IllegalArgumentException("value4는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(fileId) == true)
			throw new IllegalArgumentException("fileId는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(fileName) == true)
			throw new IllegalArgumentException("fileName는 빈 문자열을 허용하지 않습니다.");
		
		this.id = id;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.fileId = fileId;
		this.fileName = fileName;
	}

	@Override
	public String getLink() {
		return null;
	}

	@Override
	public String toString() {
		// 파일명에 확장자가 포함되어 있는지 확인하여, 확장자가 포함되어 있지 않다면 추가하여 반환한다.
		String fileName = getFileName();
		String fileExtension = getValue4();
		String temp = fileName.substring(fileName.length() - fileExtension.length());
		if (fileExtension.equalsIgnoreCase(temp) == true) {
			return fileName;
		}

		return String.format("%s.%s", getFileName(), getValue4());
	}

}
