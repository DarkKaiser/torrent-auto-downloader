package kr.co.darkkaiser.jv.common;

public class Constants {

    /*
     * 공용 환경설정 이름
     */
    public static final String SHARED_PREFERENCE_NAME = "jv_setup";

    /*
     * 로컬에 설치된 단어DB 버전(공용 환경설정 키이름)
     */
    public static final String SP_DB_VERSION = "jv_db_ver";

    /*
     * 가장 최근에 사용자가 암기했던 저장된 단어 암기 순서(공용 환경설정 키이름)@@@@@
     */
    public static final String JV_SPN_MEMORIZE_ORDER_METHOD_LATEST = "jv_memorize_order_method_latest";
    
    /*
     * 가장 최근에 사용자가 암기했던 저장된 단어 암기 순서의 인덱스(공용 환경설정 키이름)@@@@@
     */
    public static final String JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST = "jv_memorize_order_method_index_latest";

    /*
     * 리스트 정렬 방법(공용 환경설정 키이름)@@@@@
     */
    public static final String JV_SPN_LIST_SORT_METHOD = "jv_list_sort_method";
    
    /*
     * 가장 마지막에 업데이트 한 단어DB의 Max IDX 값 @@@@@
     */
    public static final String JV_SPN_LAST_UPDATED_MAX_IDX = "jv_last_updated_max_idx";

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
     * 단어 DB 다운로드 URL @@@@@ 2개 이상으로 변경
     */
    public static final String JV_DB_DOWNLOAD_URL = "http://darkkaiser.cafe24.com/data/jv2.db";

    /*
     * 단어DB 버전정보 체크 URL
     */
    public static final String JV_DB_CHECKSUM_URL = "http://darkkaiser.cafe24.com/data/vocabulary_v3.json";

}
