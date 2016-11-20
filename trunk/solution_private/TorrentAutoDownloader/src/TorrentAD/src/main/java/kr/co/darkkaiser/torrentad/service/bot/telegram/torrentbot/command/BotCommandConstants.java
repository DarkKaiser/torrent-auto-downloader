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
     * Inline 명령
     */

    // 게시판 조회 결과 목록에서 선택된 게시물의 다운로드 링크(첨부파일) 조회요청 Inline 명령
    public static final String LASR_LIST_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND = "ls";

    // 게시판 검색 결과 목록에서 선택된 게시물의 다운로드 링크(첨부파일) 조회요청 Inline 명령
    public static final String LASR_SEARCH_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND = "sc";

    // 선택된 첨부파일 다운로드 요청 Inline 명령
    public static final String DOWNLOAD_REQUEST_INLINE_COMMAND = "dl";
    
    
    /**
     * 게시판 조회 및 검색 결과 관련
     */

    // 조회 및 검색 결과 게시물의 게시물 출력 갯수
    public static final int LASR_BOARD_ITEM_OUTPUT_COUNT = 10;

    // 조회 결과 게시물 목록에서 CallbackQuery 명령
    public static final String LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND = "ls-rslt-inline";

    // 검색 결과 게시물 목록에서 CallbackQuery 명령
    public static final String LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND = "sc-rslt-inline";

	// 조회 및 검색 결과 게시물 목록에서 CallbackQuery 명령의 새로고침 InlineKyeboard 버튼
	public static final String LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT = "새로고침";
	public static final String LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA = "refresh";

	// 조회 및 검색 결과 게시물 목록에서 CallbackQuery 명령의 이전페이지 InlineKyeboard 버튼
	public static final String LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT = "《 이전 페이지";
	public static final String LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA = "prev-page";
	
	// 조회 및 검색 결과 게시물 목록에서 CallbackQuery 명령의 다음페이지 InlineKyeboard 버튼
	public static final String LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT = "다음 페이지 》";
	public static final String LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA = "next-page";
	
	
	/**
	 * 토렌트 서버 상태 조회 관련
	 */
	
    // 조회 결과 목록에서 CallbackQuery 명령
    public static final String TSSR_RESULT_CALLBACK_QUERY_COMMAND = "ts-rslt-inline";

    // 조회 결과 목록에서 CallbackQuery 명령의 새로고침 InlineKyeboard 버튼
	public static final String TSSR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT = "다시 조회하기";
	public static final String TSSR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA = "refresh";

    // 첨부파일 다운로드 성공 메시지에서 토렌트서버의 상태 조회를 위한 새로고침 InlineKyeboard 버튼
	public static final String TSSR_REFRESH_ETC_INLINE_KEYBOARD_BUTTON_TEXT = "토렌트 서버 상태 조회";
	public static final String TSSR_REFRESH_ETC_INLINE_KEYBOARD_BUTTON_DATA = "refresh-etc";

}
