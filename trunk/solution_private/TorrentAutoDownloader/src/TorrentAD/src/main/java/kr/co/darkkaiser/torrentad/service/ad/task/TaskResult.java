package kr.co.darkkaiser.torrentad.service.ad.task;

public enum TaskResult {

	NONE,
	
	/** 성공 */
	OK,
	
	/** 게시물 로드 실패 */
	BOARD_ITEMS_LOAD_FAILED,
	
	/** 예외 발생 */
	UNEXPECTED_EXCEPTION
	
}
