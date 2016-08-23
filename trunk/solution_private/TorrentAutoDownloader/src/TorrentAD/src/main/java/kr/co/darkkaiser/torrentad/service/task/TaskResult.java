package kr.co.darkkaiser.torrentad.service.task;

public enum TaskResult {

	/** 성공 */
	OK,

	/** 비밀번호 복호화 실패 */
	FAILED_DECODE_PASSWORD,
	
	/** 유효하지 않은 계정정보 */
	INVALID_ACCOUNT,

	/** 웹사이트 로그인 실패 */
	FAILED_WEBSITE_LOGIN,

	/** Task 실행 중 예외 발생 */
	UNEXPECTED_TASK_RUNNING_EXCEPTION

}
