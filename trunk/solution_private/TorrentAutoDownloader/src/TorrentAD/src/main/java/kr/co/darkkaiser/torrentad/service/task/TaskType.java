package kr.co.darkkaiser.torrentad.service.task;

public enum TaskType {

	/** Task 작업이 한번 성공하면 더이상 실행되지 않는 Task */
	ONCE,
	
	/** 주기적으로 실행되는 Task */
	PERIODIC

}
