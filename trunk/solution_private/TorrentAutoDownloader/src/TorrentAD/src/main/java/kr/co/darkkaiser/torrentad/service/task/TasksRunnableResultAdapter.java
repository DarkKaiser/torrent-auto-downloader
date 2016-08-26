package kr.co.darkkaiser.torrentad.service.task;

public final class TasksRunnableResultAdapter {

	public enum TasksRunnableResult {
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

	private TasksRunnableResult tasksExecutorServiceResult;

	private TaskResult taskResult;

	public TasksRunnableResultAdapter(TasksRunnableResult tasksExecutorServiceResult) {
		this.tasksExecutorServiceResult = tasksExecutorServiceResult;
	}

	public TasksRunnableResultAdapter(TasksRunnableResult tasksExecutorServiceResult, TaskResult taskResult) {
		this.tasksExecutorServiceResult = tasksExecutorServiceResult;
		this.taskResult = taskResult;
	}
	
	public TasksRunnableResult getResultCode() {
		return this.tasksExecutorServiceResult;
	}

	public TaskResult getTaskResultCode() {
		return this.taskResult;
	}
	
	public static TasksRunnableResultAdapter OK() {
		return new TasksRunnableResultAdapter(TasksRunnableResult.OK);
	}
	
	public static TasksRunnableResultAdapter INVALID_ACCOUNT() {
		return new TasksRunnableResultAdapter(TasksRunnableResult.INVALID_ACCOUNT);
	}
	
	public static TasksRunnableResultAdapter WEBSITE_LOGIN_FAILED() {
		return new TasksRunnableResultAdapter(TasksRunnableResult.WEBSITE_LOGIN_FAILED);
	}
	
	public static TasksRunnableResultAdapter UNEXPECTED_TASK_RUNNING_EXCEPTION() {
		return new TasksRunnableResultAdapter(TasksRunnableResult.UNEXPECTED_TASK_RUNNING_EXCEPTION);
	}
	
	public static TasksRunnableResultAdapter TASK_EXECUTION_FAILED(TaskResult taskResult) {
		return new TasksRunnableResultAdapter(TasksRunnableResult.TASK_EXECUTION_FAILED, taskResult);
	}

}
