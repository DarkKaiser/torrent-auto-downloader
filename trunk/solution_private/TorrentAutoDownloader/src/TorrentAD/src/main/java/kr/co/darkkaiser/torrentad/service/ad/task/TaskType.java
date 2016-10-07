package kr.co.darkkaiser.torrentad.service.ad.task;

public enum TaskType {

	/**
	 * 등록된 스케쥴 시간에 처리되는 태스크
	 */
	
	// 등록된 스케쥴 시간에 실행되며, Task 작업이 성공하면 더 이상 실행되지 않는 Task
	ONCE,// @@@@@ 명칭에 schedule를 넣을것인가?
	
	// 등록된 스케쥴 시간마다 주기적으로 실행되는 Task
	PERIODIC,

	/**
	 * 당장 처리되는 태스크
	 */

	// 요청이 들어오는 즉시 처리되는 Task
	// @@@@@
	IMMEDIATELY
	
}
