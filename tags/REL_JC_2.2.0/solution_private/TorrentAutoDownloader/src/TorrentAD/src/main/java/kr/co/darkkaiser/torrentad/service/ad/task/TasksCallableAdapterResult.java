package kr.co.darkkaiser.torrentad.service.ad.task;

public final class TasksCallableAdapterResult {

	public enum TasksCallableAdapterResultCode {
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

	private TasksCallableAdapterResultCode tasksCallableAdapterResultCode;

	private TaskResult taskResult;

	public TasksCallableAdapterResult(TasksCallableAdapterResultCode tasksExecutorServiceResult) {
		this.tasksCallableAdapterResultCode = tasksExecutorServiceResult;
		this.taskResult = TaskResult.NONE;
	}

	public TasksCallableAdapterResult(TasksCallableAdapterResultCode tasksExecutorServiceResult, TaskResult taskResult) {
		this.tasksCallableAdapterResultCode = tasksExecutorServiceResult;
		this.taskResult = taskResult;
	}
	
	public TasksCallableAdapterResultCode getResultCode() {
		return this.tasksCallableAdapterResultCode;
	}

	public TaskResult getTaskResultCode() {
		return this.taskResult;
	}
	
	public static TasksCallableAdapterResult OK() {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.OK);
	}

	public static TasksCallableAdapterResult OK(TaskResult taskResult) {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.OK, taskResult);
	}

	public static TasksCallableAdapterResult UNEXPECTED_EXCEPTION() {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.UNEXPECTED_EXCEPTION);
	}

	public static TasksCallableAdapterResult INVALID_ACCOUNT() {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.INVALID_ACCOUNT);
	}
	
	public static TasksCallableAdapterResult WEBSITE_LOGIN_FAILED() {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.WEBSITE_LOGIN_FAILED);
	}
	
	public static TasksCallableAdapterResult UNEXPECTED_TASK_RUNNING_EXCEPTION() {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.UNEXPECTED_TASK_RUNNING_EXCEPTION);
	}
	
	public static TasksCallableAdapterResult TASK_EXECUTION_FAILED(TaskResult taskResult) {
		return new TasksCallableAdapterResult(TasksCallableAdapterResultCode.TASK_EXECUTION_FAILED, taskResult);
	}

}
