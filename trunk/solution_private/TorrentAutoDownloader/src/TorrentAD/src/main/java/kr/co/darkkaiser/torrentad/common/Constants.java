package kr.co.darkkaiser.torrentad.common;

public final class Constants {

	public static final String APP_NAME = "TorrentAD";
	public static final String APP_VERSION = "0.0.1";
	public static final String APP_CONFIG_FILE_NAME = "torrentad.xml";

	public static final String APP_CONFIG_TAG_PERIODIC_TASK = "torrentad-periodic-task";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_BOARD_NAME = "board-name";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS_TITLE = "search-keywords-title";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS_FILE = "search-keywords-file";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD = "search-keyword";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_MODE_ATTR = "mode";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_ITEM = "item";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_LATEST_DOWNLOAD_IDENTIFIER = "latest-download-identifier";
	
	public static final String APP_CONFIG_TAG_WEBSITE_NAME = "website-name";
	public static final String APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID = "website-account-id";
	public static final String APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD = "website-account-password";
	public static final String APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION = "download-file-write-location";
	public static final String APP_CONFIG_TAG_TASK_EXECUTE_INTERVAL_TIME_SECOND = "task-execute-interval-time-second";

	// 유효하지 않은 다운로드 게시물 식별자
	public static final long INVALID_DOWNLOAD_IDENTIFIER_VALUE = 0;

	private Constants() {
	}

}
