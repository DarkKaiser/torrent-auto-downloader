package com.darkkaiser.torrentad.service.ad.task.scheduled;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;
import com.darkkaiser.torrentad.service.ad.task.TasksCallableAdapter;
import com.darkkaiser.torrentad.service.ad.task.TasksCallableAdapterResult;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import com.darkkaiser.torrentad.website.WebSiteConnector;
import com.darkkaiser.torrentad.website.WebSiteHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public final class ScheduledTasksCallableAdapter implements TasksCallableAdapter {

	private final WebSiteConnector siteConnector;

	private final List<ScheduledTask> tasks;

	public ScheduledTasksCallableAdapter(final Configuration configuration, final MetadataRepository metadataRepository) throws Exception {
		Objects.requireNonNull(configuration, "configuration");

		this.siteConnector = new DefaultWebSiteConnector(ScheduledTasksCallableAdapter.class.getSimpleName(), configuration);

		// Task 목록을 생성한다.
		this.tasks = ScheduledTasksGenerator.generate(configuration, metadataRepository, this.siteConnector.getSite());
	}

	@Override
	public TasksCallableAdapterResult call() {
		try {
			if (this.siteConnector.login() == false)
				return TasksCallableAdapterResult.WEBSITE_LOGIN_FAILED();

			WebSiteHandler handler = (WebSiteHandler) this.siteConnector.getConnection();

			// 마지막으로 실행된 Task의 성공 또는 실패코드를 반환한다.
			TasksCallableAdapterResult result = TasksCallableAdapterResult.OK();

			for (final ScheduledTask task : this.tasks) {
				log.debug("Task를 실행합니다.(Task:{})", task.getTaskDescription());

				try {
					TaskResult taskResult = task.run(handler);
					if (taskResult != TaskResult.OK) {
						log.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, task.getTaskDescription());
						result = TasksCallableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
					} else {
						log.debug("Task 실행이 완료되었습니다.(Task:{})", task.getTaskDescription());
						result = TasksCallableAdapterResult.OK(TaskResult.OK);
					}
				} catch (final Throwable e) {
					log.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", task.getTaskDescription(), e);
					result = TasksCallableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
				}
			}

			this.siteConnector.logout();

			return result;
		} catch (final Exception e) {
			log.error(null, e);
		}

		return TasksCallableAdapterResult.UNEXPECTED_EXCEPTION();
	}

}
