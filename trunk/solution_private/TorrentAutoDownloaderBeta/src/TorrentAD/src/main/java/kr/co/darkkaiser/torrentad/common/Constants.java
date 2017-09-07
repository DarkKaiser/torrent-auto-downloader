package kr.co.darkkaiser.torrentad.common;

public final class Constants {

	public static final String APP_NAME = "TorrentAD";
	public static final String APP_VERSION = "0.1.0";
	
	// AD Service Task에서 다운로드 진행중인 파일의 임시 확장자
	public static final String AD_SERVICE_TASK_NOTYET_DOWNLOADED_FILE_EXTENSION = ".notyet";

	
	/**
	 * 프로그램 환경설정 관련
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

	// AU Service 설정값 항목
	public static final String APP_CONFIG_TAG_FTP_SERVER_HOST = "ftp-server-host";
	public static final String APP_CONFIG_TAG_FTP_SERVER_PORT = "ftp-server-port";
	public static final String APP_CONFIG_TAG_FTP_SERVER_ACCOUNT_ID = "ftp-server-account-id";
	public static final String APP_CONFIG_TAG_FTP_SERVER_ACCOUNT_PASSWORD = "ftp-server-account-password";
	public static final String APP_CONFIG_TAG_FTP_SERVER_UPLOAD_LOCATION = "ftp-server-upload-location";
	public static final String APP_CONFIG_TAG_TORRENT_RPC_URL = "torrent-rpc-url";
	public static final String APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID = "torrent-rpc-account-id";
	public static final String APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD = "torrent-rpc-account-password";
	public static final String APP_CONFIG_TAG_TORRENT_SUPERVISORY_CONTROL_INTERVAL_TIME_SECOND = "torrent-supervisory-control-interval-time-second";
	public static final String APP_CONFIG_TAG_MAX_CONCURRENT_DOWNLOADING_TORRENT_COUNT = "max-concurrent-downloading-torrent-count";
	public static final String APP_CONFIG_TAG_DOWNLOAD_FILE_WATCH_INTERVAL_TIME_SECOND = "download-file-watch-interval-time-second";

	// Bot Service 설정값 항목
	public static final String APP_CONFIG_TAG_TELEGRAM_TORRENTBOT_USERNAME = "telegram-torrentbot-username";
	public static final String APP_CONFIG_TAG_TELEGRAM_TORRENTBOT_TOKEN = "telegram-torrentbot-token";

	
	/**
	 * AD Service 관련
	 */
	
	// AD Service Task 항목
	public static final String APP_CONFIG_TAG_TASK_ATTR_ID = "id";
	public static final String APP_CONFIG_TAG_TASK_ATTR_DESCRIPTION = "description";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK = "torrentad-periodic-scheduled-task";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_BOARD_NAME = "board-name";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORDS = "search-keywords";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORDS_ATTR_TYPE = "type";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORD = "search-keyword";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORD_ATTR_MODE = "mode";
	public static final String APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORD_ITEM = "item";


	/*
	 * 메타데이터 저장소 파일 관련
	 */

	//------------
	// AD Service
	//------------

	// AD Service 메타데이터 저장소 파일명
	public static final String AD_SERVICE_METADATA_REPOSITORY_FILE_NAME = "torrentad.ad.properties";

	// 최근에 다운로드 받은 게시물 식별자
	// 게시물 식별자 이후에 등록된 게시물을 검색 대상으로 한다.
	// 게시물 식별자가 비어있는 경우는 읽어들인 게시물의 모든 항목을 검색 대상으로 한다.
	public static final String AD_SERVICE_MR_KEY_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER = "latest-download-board-item-identifier";

	//-------------
	// Bot Service
	//-------------

	// Bot Service 메타데이터 저장소 파일명
	public static final String BOT_SERVICE_METADATA_REPOSITORY_FILE_NAME = "torrentad.bot.properties";

	// Bot Service
	// 등록된 모든 대화방의 ID 목록
	public static final String BOT_SERVICE_MR_KEY_REGISTERED_CHAT_IDS = "chat-ids";
    public static final String BOT_SERVICE_MR_KEY_REGISTERED_CHAT_IDS_SEPARATOR = ",";

	// Bot Service
	public static final String BOT_SERVICE_MR_KEY_CHAT_ID_PREFIX = "chat-id";
	
	// Bot Service
	// 대화방에서 조회 및 검색하기 위해 선택한 게시판의 코드명
	public static final String BOT_SERVICE_MR_KEY_CHAT_ID_SUBKEY_BOARD_CODE = "board-code";

	private Constants() {
	}

}
