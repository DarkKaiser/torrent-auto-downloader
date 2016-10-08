package kr.co.darkkaiser.torrentad.service.ad.task;

public final class TasksRunnableAdapterResult {

	public enum TasksRunnableAdapterResultCode {
		/** 성공 */
		OK,

		/** 예외 발생 */
		UNEXPECTED_EXCEPTION,

		/** 유효하지 않은 웹사이트 계정정보 */
		INVALID_ACCOUNT,

		/** 웹사이트 로그인 실패 */
		WEBSITE_LOGIN_FAILED,

		/** Task 실행 중 예외 발생 */
		UNEXPECTED_TASK_RUNNING_EXCEPTION,

		/** Task 실행 실패 */
		TASK_EXECUTION_FAILED
	}

	private TasksRunnableAdapterResultCode tasksRunnableAdapterResultCode;

	private TaskResult taskResult;

	public TasksRunnableAdapterResult(TasksRunnableAdapterResultCode tasksExecutorServiceResult) {
		this.tasksRunnableAdapterResultCode = tasksExecutorServiceResult;
		this.taskResult = TaskResult.NONE;
	}

	public TasksRunnableAdapterResult(TasksRunnableAdapterResultCode tasksExecutorServiceResult, TaskResult taskResult) {
		this.tasksRunnableAdapterResultCode = tasksExecutorServiceResult;
		this.taskResult = taskResult;
	}
	
	public TasksRunnableAdapterResultCode getResultCode() {
		return this.tasksRunnableAdapterResultCode;
	}

	public TaskResult getTaskResultCode() {
		return this.taskResult;
	}
	
	public static TasksRunnableAdapterResult OK() {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.OK);
	}

	public static TasksRunnableAdapterResult OK(TaskResult taskResult) {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.OK, taskResult);
	}

	public static TasksRunnableAdapterResult UNEXPECTED_EXCEPTION() {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.UNEXPECTED_EXCEPTION);
	}

	public static TasksRunnableAdapterResult INVALID_ACCOUNT() {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.INVALID_ACCOUNT);
	}
	
	public static TasksRunnableAdapterResult WEBSITE_LOGIN_FAILED() {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.WEBSITE_LOGIN_FAILED);
	}
	
	public static TasksRunnableAdapterResult UNEXPECTED_TASK_RUNNING_EXCEPTION() {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.UNEXPECTED_TASK_RUNNING_EXCEPTION);
	}
	
	public static TasksRunnableAdapterResult TASK_EXECUTION_FAILED(TaskResult taskResult) {
		return new TasksRunnableAdapterResult(TasksRunnableAdapterResultCode.TASK_EXECUTION_FAILED, taskResult);
	}

}
