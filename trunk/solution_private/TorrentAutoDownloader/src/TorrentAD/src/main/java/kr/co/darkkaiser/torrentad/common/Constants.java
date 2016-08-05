package kr.co.darkkaiser.torrentad.common;

public final class Constants {

	// 디스패쳐 서버 환경설정 파일
	public static final String DISPATCHER_SERVER_CONFIG_FILEPATH = "fbis-dispatcher.xml";

	// 디스패쳐 서버 ID의 최대 길이
	public static final int MAX_DISPATCHER_SERVERID_LENGTH = 10;

	// 인증/검색 서버ID의 최대 길이
	public static final int MAX_MATCHER_SERVERID_LENGTH = 10;

	// 유효하지 않은 시간값
	public static final long INVALID_TIME_VALUE = -1;

	// 유효하지 않은 네트워크 포트 번호
	public static final int INVALID_PORT_NUMBER = -1;
	
	// 유효하지 않은 매칭스코어 값
	public static final int INVALID_MATCHING_SCORE_VALUE = -1;
	
	// 로그파일에 적용되는 날짜 형식
	public static final String LOG_FILENAME_DATE_FORMAT = "yyyyMMdd";

	private Constants() {
	}

}
