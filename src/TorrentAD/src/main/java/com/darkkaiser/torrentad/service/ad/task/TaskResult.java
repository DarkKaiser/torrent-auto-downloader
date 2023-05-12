package com.darkkaiser.torrentad.service.ad.task;

public enum TaskResult {

	NONE,
	
	/** 성공 */
	OK,
	
	/** 실패 */
	FAILED,

	/** 예외 발생 */
	UNEXPECTED_EXCEPTION
	
}
