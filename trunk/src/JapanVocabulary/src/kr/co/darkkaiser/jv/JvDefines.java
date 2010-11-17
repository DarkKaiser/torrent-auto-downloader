package kr.co.darkkaiser.jv;

public class JvDefines {

    /*
     * 공용 환경설정 이름
     */
    public static final String JV_SHARED_PREFERENCE_NAME = "jv_setup";
    
    /*
     * 로컬에 설치된 단어 DB 버전 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_DB_VERSION = "jv_db_ver";
    
    /*
     * 암기대상 단어 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_MEMORIZE_TARGET_ITEM = "jv_memorize_target_item";
    
    /*
     * 다음 단어로 전환시 페이드 효과 적용 여부 항목 이름(공용 환경설정 키이름)
     */
    public static final String JV_SPN_FADE_EFFECT_NEXT_VOCABULARY = "jv_fade_effect_next_vocabulary";

    /*
     * 리스트 정렬 방법(공용 환경설정 키이름)
     */
    public static final String JV_SPN_LIST_SORT_METHOD = "jv_list_sort_method";

    /*
	 * SDCARD에 생성되는 프로그램 폴더명
	 */
	public static final String JV_MAIN_FOLDER_NAME = "JapanVocabulary";
	
	/*
	 * 일본어 단어 DB 파일명
	 */
    public static final String JV_VOCABULARY_DB = "jv2.db";

    /*
     * 사용자의 단어에 대한 정보를 저장한 파일명
     */
    public static final String JV_USER_VOCABULARY_INFO_FILE = "jv2_user.db";
    
    /*
     * 단어 DB 버전 정보 확인 URL
     */
    public static final String JV_DB_VERSION_CHECK_URL = "http://darkkaiser.cafe24.com/data/jv2_db_update_check.php";
    
    /*
     * 단어 DB 다운로드 URL
     */
    public static final String JV_DB_DOWNLOAD_URL = "http://darkkaiser.cafe24.com/data/jv2.db";

}
