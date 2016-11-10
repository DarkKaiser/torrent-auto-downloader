package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

public final class BotCommandConstants {

	// TelegramBot 명령의 최대 길이
	public static final int BOT_COMMAND_MAX_LENGTH = 32;

	// TelegramBot 명령 이니셜 문자
	public static final String BOT_COMMAND_INITIAL_CHARACTER = "/";

	// TelegramBot 명령 또는 파라메터간의 구분자
    public static final String BOT_COMMAND_PARAMETER_SEPARATOR = " ";

    // ComplexBotCommand의 구분자
    public static final String COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR = "_";

    // 유효하지 않은 TelegramBot 메시지ID
    public static final int INVALID_BOT_COMMAND_MESSAGE_ID = -1;

    
    /**
     * 게시판 조회 및 검색 결과
     */

    // 조회 및 검색 결과 게시물 출력 갯수
    public static final int LASR_OUTPUT_BOARD_ITEM_COUNT = 5;

    // 조회 결과 게시물 목록내의 인라인 명령
    public static final String LASR_LIST_RESULT_INLINE_COMMAND = "ls-rslt-inline";

    // 검색 결과 게시물 목록내의 인라인 명령
    public static final String LASR_SEARCH_RESULT_INLINE_COMMAND = "sc-rslt-inline";

	// 조회 및 검색 결과 게시물 목록내의 인라인 키보드 새로고침 버튼
	public static final String LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT = "새로고침";
	public static final String LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA = "refresh";

	// 조회 및 검색 결과 목록내의 인라인 키보드 이전페이지 버튼
	public static final String LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT = "이전 페이지";
	public static final String LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA = "prev-page";
	
	// 조회 및 검색 결과 목록내의 인라인 키보드 다음페이지 버튼
	public static final String LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT = "다음 페이지";
	public static final String LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA = "next-page";

}
