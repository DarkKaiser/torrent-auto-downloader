package kr.co.darkkaiser.torrentad.service.task;

public final class TasksExecutorServiceResultAdapter {

	public enum TasksExecutorServiceResult {
		/** 성공 */
		OK,

		/** 유효하지 않은 웹사이트 계정정보 */
		INVALID_ACCOUNT,

		/** 웹사이트 로그인 실패 */
		WEBSITE_LOGIN_FAILED,

		/** Task 실행 중 예외 발생 */
		UNEXPECTED_TASK_RUNNING_EXCEPTION,

		/** Task 실행 실패 */
		TASK_EXECUTION_FAILED
	}

	private TasksExecutorServiceResult tasksExecutorServiceResult;

	private TaskResult taskResult;

	public TasksExecutorServiceResultAdapter(TasksExecutorServiceResult tasksExecutorServiceResult) {
		this.tasksExecutorServiceResult = tasksExecutorServiceResult;
	}

	public TasksExecutorServiceResultAdapter(TasksExecutorServiceResult tasksExecutorServiceResult, TaskResult taskResult) {
		this.tasksExecutorServiceResult = tasksExecutorServiceResult;
		this.taskResult = taskResult;
	}
	
	public TasksExecutorServiceResult getResultCode() {
		return this.tasksExecutorServiceResult;
	}

	public TaskResult getTaskResultCode() {
		return this.taskResult;
	}
	
	public static TasksExecutorServiceResultAdapter OK() {
		return new TasksExecutorServiceResultAdapter(TasksExecutorServiceResult.OK);
	}
	
	public static TasksExecutorServiceResultAdapter INVALID_ACCOUNT() {
		return new TasksExecutorServiceResultAdapter(TasksExecutorServiceResult.INVALID_ACCOUNT);
	}
	
	public static TasksExecutorServiceResultAdapter WEBSITE_LOGIN_FAILED() {
		return new TasksExecutorServiceResultAdapter(TasksExecutorServiceResult.WEBSITE_LOGIN_FAILED);
	}
	
	public static TasksExecutorServiceResultAdapter UNEXPECTED_TASK_RUNNING_EXCEPTION() {
		return new TasksExecutorServiceResultAdapter(TasksExecutorServiceResult.UNEXPECTED_TASK_RUNNING_EXCEPTION);
	}
	
	public static TasksExecutorServiceResultAdapter TASK_EXECUTION_FAILED(TaskResult taskResult) {
		return new TasksExecutorServiceResultAdapter(TasksExecutorServiceResult.TASK_EXECUTION_FAILED, taskResult);
	}

}
