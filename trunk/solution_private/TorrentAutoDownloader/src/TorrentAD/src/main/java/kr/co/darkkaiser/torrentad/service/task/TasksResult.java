package kr.co.darkkaiser.torrentad.service.task;

public enum TasksResult {

	/** 성공 */
	OK,

	/** 웹사이트 비밀번호 복호화 실패 */
	PASSWORD_DECRYPTION_FAILED,

	/** 유효하지 않은 웹사이트 이름 */
	INVALID_WEBSITE_NAME,

	/** 유효하지 않은 웹사이트 계정정보 */
	INVALID_ACCOUNT,

	/** 웹사이트 로그인 실패 */
	WEBSITE_LOGIN_FAILED,

	/** Task 실행 중 예외 발생 */
	UNEXPECTED_TASK_RUNNING_EXCEPTION

}
