package kr.co.darkkaiser.torrentad.service.ad.task;

public enum TaskType {

	/**
	 * 등록된 스케쥴 시간에 처리되는 Task
	 */
	
	// 등록된 스케쥴 시간마다 실행되며, Task 작업이 성공하면 더 이상 실행되지 않는 Task
	ONCE_SCHEDULED,

	// 등록된 스케쥴 시간마다 주기적으로 실행되는 Task
	PERIODIC_SCHEDULED,

	/**
	 * 당장 처리되는 Task
	 */

	// 요청이 들어오는 즉시 처리되는 Task
	IMMEDIATELY

}
