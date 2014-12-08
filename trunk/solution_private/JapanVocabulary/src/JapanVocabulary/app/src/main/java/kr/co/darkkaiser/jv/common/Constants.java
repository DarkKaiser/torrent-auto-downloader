package kr.co.darkkaiser.jv.common;

public class Constants {

    /*
     * 공용 환경설정 이름
     */
    public static final String SHARED_PREFERENCE_NAME = "jv_setup";

    /*
     * 로컬에 설치된 단어DB 버전(공용 환경설정 키이름)
     */
    public static final String SPKEY_DB_VERSION = "jv_db_ver";

    /*
     * 가장 마지막에 업데이트 한 단어DB의 IDX 최대값(공용 환경설정 키이름)
     */
    public static final String SPKEY_LAST_UPDATED_MAX_VOCABULARY_IDX = "jv_last_updated_max_idx";

    /*
     * 가장 최근에 사용자가 암기했던 저장된 단어암기 순서(공용 환경설정 키이름)
     */
    public static final String SPKEY_LATEST_VOCABULARY_MEMORIZE_ORDER = "latest_vocabulary_memorize_order";

    /*
     * 가장 최근에 사용자가 암기했던 저장된 단어암기 순서의 인덱스(공용 환경설정 키이름)
     */
    public static final String SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION = "latest_vocabulary_memorize_position";

    /*
     * 검색 결과리스트 정렬 방법(공용 환경설정 키이름)
     */
    public static final String SPKEY_SEARCH_LIST_SORT = "search_list_sort";

    /*
	 * 단어DB 파일명
	 */
    public static final String VOCABULARY_DB_FILENAME_V2 = "jv2.db";
    public static final String VOCABULARY_DB_FILENAME_V3 = "vocabulary_v3.db";

    /*
     * 사용자의 암기정보를 저장한 DB 파일명
     */
    public static final String USER_DB_FILENAME_V2 = "jv2_user.db";
    public static final String USER_DB_FILENAME_V3 = "vocabulary_user_v3.db";

    /*
     * 단어 DB 다운로드 URL
     */
    public static final String VOCABULARY_DB_DOWNLOAD_URL_1 = "http://darkkaiser.gonetis.com:55580/download/vocabulary_v3.db";
    public static final String VOCABULARY_DB_DOWNLOAD_URL_2 = "http://darkkaiser.cafe24.com/data/vocabulary_v3.db";
    public static final String VOCABULARY_DB_DOWNLOAD_URL_3 = "http://darkkaiser.gonetis.com:55580/download/vocabulary_v4.db";

    /*
     * 단어DB 버전정보 체크 URL
     */
    public static final String VOCABULARY_DB_CHECKSUM_URL = "http://darkkaiser.cafe24.com/data/vocabulary_v3.json";

}
