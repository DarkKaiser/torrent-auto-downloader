package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.ad.task.Task;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistryImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksRunnableAdapter;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksRunnableAdapterResult;
import kr.co.darkkaiser.torrentad.service.ad.task.scheduled.ScheduledTaskGenerator;

public final class ImmediatelyTasksRunnableAdapter implements TasksRunnableAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ImmediatelyTasksRunnableAdapter.class);

	private final Task task;

	private final Configuration configuration;
	
	private final TaskMetadataRegistry taskMetadataRegistry;
	
	public ImmediatelyTasksRunnableAdapter(Configuration configuration) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.taskMetadataRegistry = new TaskMetadataRegistryImpl(Constants.AD_SERVICE_TASK_METADATA_FILE_NAME);

		// Task를 생성한다.
//		this.tasks = ScheduledTaskGenerator.generate(this.configuration, this.taskMetadataRegistry, this.connector.getSite());
		this.task = null;
	}

	@Override
	public TasksRunnableAdapterResult call() throws Exception {
		try {
			return call0();
		} catch (Exception e) {
			logger.error(null, e);
		}

		return TasksRunnableAdapterResult.UNEXPECTED_EXCEPTION();
	}

	private TasksRunnableAdapterResult call0() throws Exception {
//		if (this.connector.login() == false)
//			return TasksRunnableAdapterResult.WEBSITE_LOGIN_FAILED();
//
//		WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();
//
//		// 마지막으로 실행된 Task의 성공 또는 실패코드를 반환한다.
//		TasksRunnableAdapterResult result = TasksRunnableAdapterResult.OK();
//
//		for (Task task : this.tasks) {
//			logger.debug("Task를 실행합니다.(Task:{})", task.getTaskDescription());
//
//			try {
//				TaskResult taskResult = task.run(handler);
//				if (taskResult != TaskResult.OK) {
//					logger.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, task.getTaskDescription());
//					result = TasksRunnableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
//				} else {
//					logger.debug("Task 실행이 완료되었습니다.(Task:{})", task.getTaskDescription());
//					result = TasksRunnableAdapterResult.OK(TaskResult.OK);
//				}
//			} catch (Throwable e) {
//				logger.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", task.getTaskDescription(), e);
//				result = TasksRunnableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
//			}
//		}
//
//		this.connector.logout();
//
//		return result;
		return null;
	}

}
