package kr.co.darkkaiser.jv.common;

//@@@@@ todo
public class Constants {

    /*
     * 공용 환경설정 이름
     */
    public static final String JV_SHARED_PREFERENCE_NAME = "jv_setup";
    
    /*
     * 로컬에 설치된 단어DB 버전 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_DB_VERSION = "jv_db_ver";

    /*
     * 암기대상 단어 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_MEMORIZE_TARGET_ITEM = "jv_memorize_target_item";
    
    /*
     * 암기 대상 단어가 출력되는 아랫부분에 단어의 뜻 출력 여부 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_SHOW_VOCABULARY_TRANSLATION = "jv_show_vocabulary_translation";
    
    /*
     * 다음 단어로 전환시 페이드 효과 적용 여부 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_FADE_EFFECT_NEXT_VOCABULARY = "jv_fade_effect_next_vocabulary";

    /*
     * 다음 단어로 전환시 휴대폰 진동 여부 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_VIBRATE_NEXT_VOCABULARY = "jv_vibrate_next_vocabulary";

    /*
     * 단어 암기 순서 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_MEMORIZE_ORDER_METHOD = "jv_memorize_order_method";

    /*
     * 가장 최근에 사용자가 암기했던 저장된 단어 암기 순서 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_MEMORIZE_ORDER_METHOD_LATEST = "jv_memorize_order_method_latest";
    
    /*
     * 가장 최근에 사용자가 암기했던 저장된 단어 암기 순서의 인덱스 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST = "jv_memorize_order_method_index_latest";

    /*
     * 리스트 정렬 방법(공용 환경설정 키이름)
     */
    public static final String JV_SPN_LIST_SORT_METHOD = "jv_list_sort_method";
    
    /*
     * 시작시 단어DB 업데이트 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_VOCABULARY_UPDATE_ON_STARTED = "jv_vocabulary_update_on_started";

    /*
     * 가장 마지막에 업데이트 된 단어 DB의 Max IDX 값
     */
    public static final String JV_SPN_LAST_UPDATED_MAX_IDX = "jv_last_updated_max_idx";

    /*
	 * sdcard에 생성되는 프로그램 폴더명
	 */
	public static final String JV_MAIN_FOLDER_NAME = "JapanVocabulary";
	
	/*
	 * 일본어 단어 DB 파일명 @@@@@
	 */
    public static final String JV_VOCABULARY_DB = "jv2.db";

    /*
     * 사용자의 단어에 대한 정보를 저장한 파일명 @@@@@
     */
    public static final String JV_USER_VOCABULARY_INFO_FILE = "jv2_user.db";
    
    /*
     * 단어 DB 다운로드 URL @@@@@
     */
    public static final String JV_DB_DOWNLOAD_URL = "http://darkkaiser.cafe24.com/data/jv2.db";

    /*
     * 단어 DB 버전 정보 확인 URL @@@@@ url 변경
     */
    public static final String JV_DB_CHECKSUM_URL = "http://darkkaiser.cafe24.com/data/jvdb.json";

}
