package kr.co.darkkaiser.torrentad.common;

public final class Constants {

	public static final String APP_NAME = "TorrentAD";
	public static final String APP_VERSION = "0.0.1";

	
	/**
	 * 프로그램 환경설정 파일 관련
	 */
	public static final String APP_CONFIG_FILE_NAME = "torrentad.xml";

	// Service의 설정값 항목 그룹 태그
	public static final String APP_CONFIG_TAG_SERVICE_CONFIG_VALUES = "torrentad-service-config-values";

	// AD Service 설정값 항목
	public static final String APP_CONFIG_TAG_WEBSITE_NAME = "website-name";
	public static final String APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID = "website-account-id";
	public static final String APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD = "website-account-password";
	public static final String APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION = "download-file-write-location";
	public static final String APP_CONFIG_TAG_TASK_EXECUTE_INTERVAL_TIME_SECOND = "task-execute-interval-time-second";

	// SupervisoryControl Service 설정값 항목
	public static final String APP_CONFIG_TAG_FTP_ACCOUNT_ID = "ftp-account-id";
	public static final String APP_CONFIG_TAG_FTP_ACCOUNT_PASSWORD = "ftp-account-password";
	public static final String APP_CONFIG_TAG_TORRENT_ACCOUNT_ID = "torrent-account-id";
	public static final String APP_CONFIG_TAG_TORRENT_ACCOUNT_PASSWORD = "torrent-account-password";

	// AD Service Task
	public static final String APP_CONFIG_TAG_TASK_ATTR_ID = "id";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK = "torrentad-periodic-task";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_BOARD_NAME = "board-name";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS = "search-keywords";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS_ATTR_TYPE = "type";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD = "search-keyword";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_ATTR_MODE = "mode";
	public static final String APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_ITEM = "item";
	
	
	/*
	 * AD Service Task 메타데이터 관련
	 */
	public static final String APP_AD_SERVICE_TASK_METADATA_FILE_NAME = "torrentad.properties";

	// 최근에 다운로드 받은 게시물 식별자
	// 게시물 식별자 이후에 등록된 게시물을 검색 대상으로 한다.
	// 게시물 식별자가 비어있는 경우는 읽어들인 게시물의 모든 항목을 검색 대상으로 한다.
	public static final String APP_AD_SERVICE_TASK_METADATA_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER = "latest-download-board-item-identifier";

	private Constants() {
	}

}
