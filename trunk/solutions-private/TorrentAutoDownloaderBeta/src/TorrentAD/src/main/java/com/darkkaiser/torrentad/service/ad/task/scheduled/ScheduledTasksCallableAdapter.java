package com.darkkaiser.torrentad.service.ad.task.scheduled;

import java.util.List;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.service.ad.task.TasksCallableAdapter;
import com.darkkaiser.torrentad.service.ad.task.TasksCallableAdapterResult;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import com.darkkaiser.torrentad.website.WebSiteConnector;
import com.darkkaiser.torrentad.website.WebSiteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScheduledTasksCallableAdapter implements TasksCallableAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksCallableAdapter.class);

	private WebSiteConnector siteConnector;

	private final List<ScheduledTask> tasks;

	private final Configuration configuration;
	
	public ScheduledTasksCallableAdapter(Configuration configuration, MetadataRepository metadataRepository) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.siteConnector = new DefaultWebSiteConnector(ScheduledTasksCallableAdapter.class.getSimpleName(), configuration);

		// Task 목록을 생성한다.
		this.tasks = ScheduledTasksGenerator.generate(this.configuration, metadataRepository, this.siteConnector.getSite());
	}

	@Override
	public TasksCallableAdapterResult call() throws Exception {
		try {
			if (this.siteConnector.login() == false)
				return TasksCallableAdapterResult.WEBSITE_LOGIN_FAILED();

			WebSiteHandler handler = (WebSiteHandler) this.siteConnector.getConnection();

			// 마지막으로 실행된 Task의 성공 또는 실패코드를 반환한다.
			TasksCallableAdapterResult result = TasksCallableAdapterResult.OK();

			for (ScheduledTask task : this.tasks) {
				logger.debug("Task를 실행합니다.(Task:{})", task.getTaskDescription());

				try {
					TaskResult taskResult = task.run(handler);
					if (taskResult != TaskResult.OK) {
						logger.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, task.getTaskDescription());
						result = TasksCallableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
					} else {
						logger.debug("Task 실행이 완료되었습니다.(Task:{})", task.getTaskDescription());
						result = TasksCallableAdapterResult.OK(TaskResult.OK);
					}
				} catch (Throwable e) {
					logger.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", task.getTaskDescription(), e);
					result = TasksCallableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
				}
			}

			this.siteConnector.logout();

			return result;
		} catch (Exception e) {
			logger.error(null, e);
		}

		return TasksCallableAdapterResult.UNEXPECTED_EXCEPTION();
	}

}
