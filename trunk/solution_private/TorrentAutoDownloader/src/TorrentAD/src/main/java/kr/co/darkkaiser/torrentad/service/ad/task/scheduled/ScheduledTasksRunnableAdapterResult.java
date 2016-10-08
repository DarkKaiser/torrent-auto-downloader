package kr.co.darkkaiser.torrentad.service.ad.task.scheduled;

import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;

public final class ScheduledTasksRunnableAdapterResult {

	public enum ScheduledTasksRunnableAdapterResultCode {
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

	private ScheduledTasksRunnableAdapterResultCode tasksRunnableAdapterResultCode;

	private TaskResult taskResult;

	public ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode tasksExecutorServiceResult) {
		this.tasksRunnableAdapterResultCode = tasksExecutorServiceResult;
		this.taskResult = TaskResult.NONE;
	}

	public ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode tasksExecutorServiceResult, TaskResult taskResult) {
		this.tasksRunnableAdapterResultCode = tasksExecutorServiceResult;
		this.taskResult = taskResult;
	}
	
	public ScheduledTasksRunnableAdapterResultCode getResultCode() {
		return this.tasksRunnableAdapterResultCode;
	}

	public TaskResult getTaskResultCode() {
		return this.taskResult;
	}
	
	public static ScheduledTasksRunnableAdapterResult OK() {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.OK);
	}

	public static ScheduledTasksRunnableAdapterResult OK(TaskResult taskResult) {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.OK, taskResult);
	}

	public static ScheduledTasksRunnableAdapterResult UNEXPECTED_EXCEPTION() {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.UNEXPECTED_EXCEPTION);
	}

	public static ScheduledTasksRunnableAdapterResult INVALID_ACCOUNT() {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.INVALID_ACCOUNT);
	}
	
	public static ScheduledTasksRunnableAdapterResult WEBSITE_LOGIN_FAILED() {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.WEBSITE_LOGIN_FAILED);
	}
	
	public static ScheduledTasksRunnableAdapterResult UNEXPECTED_TASK_RUNNING_EXCEPTION() {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.UNEXPECTED_TASK_RUNNING_EXCEPTION);
	}
	
	public static ScheduledTasksRunnableAdapterResult TASK_EXECUTION_FAILED(TaskResult taskResult) {
		return new ScheduledTasksRunnableAdapterResult(ScheduledTasksRunnableAdapterResultCode.TASK_EXECUTION_FAILED, taskResult);
	}

}
