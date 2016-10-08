package kr.co.darkkaiser.torrentad.service.ad.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public final class ScheduledTasksRunnableAdapter implements Callable<ScheduledTasksRunnableAdapterResult> {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksRunnableAdapter.class);

	private WebSiteConnector connector;

	private final List<Task> tasks;

	private final Configuration configuration;
	
	private final TaskMetadataRegistry taskMetadataRegistry;

	public ScheduledTasksRunnableAdapter(Configuration configuration) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.taskMetadataRegistry = new DefaultTaskMetadataRegistry(Constants.AD_SERVICE_TASK_METADATA_FILE_NAME);

		this.connector = new DefaultWebSiteConnector(configuration);

		// Task 목록을 생성한다.
		this.tasks = ScheduledTaskGenerator.generate(this.configuration, this.taskMetadataRegistry, this.connector.getSite());
	}

	@Override
	public ScheduledTasksRunnableAdapterResult call() throws Exception {
		logger.info("새 토렌트 파일 확인을 시작합니다.");

		try {
			return call0();
		} catch (Exception e) {
			logger.error("새 토렌트 파일 확인중에 예외가 발생하였습니다.", e);
		} finally {
			logger.info("새 토렌트 파일 확인이 종료되었습니다.");
		}

		return ScheduledTasksRunnableAdapterResult.UNEXPECTED_EXCEPTION();
	}

	private ScheduledTasksRunnableAdapterResult call0() throws Exception {
		if (this.connector.login() == false)
			return ScheduledTasksRunnableAdapterResult.WEBSITE_LOGIN_FAILED();

		WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();

		// 마지막으로 실행된 Task의 성공 또는 실패코드를 반환한다.
		ScheduledTasksRunnableAdapterResult result = ScheduledTasksRunnableAdapterResult.OK();

		for (Task task : this.tasks) {
			logger.debug("Task를 실행합니다.(Task:{})", task.getTaskDescription());

			try {
				TaskResult taskResult = task.run(handler);
				if (taskResult != TaskResult.OK) {
					logger.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, task.getTaskDescription());
					result = ScheduledTasksRunnableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
				} else {
					logger.debug("Task 실행이 완료되었습니다.(Task:{})", task.getTaskDescription());
					result = ScheduledTasksRunnableAdapterResult.OK(TaskResult.OK);
				}
			} catch (Throwable e) {
				logger.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", task.getTaskDescription(), e);
				result = ScheduledTasksRunnableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
			}
		}

		this.connector.logout();

		return result;
	}

}
