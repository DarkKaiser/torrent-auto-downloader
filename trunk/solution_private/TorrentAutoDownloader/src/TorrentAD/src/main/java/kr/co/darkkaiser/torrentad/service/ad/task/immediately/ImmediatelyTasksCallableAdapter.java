package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskFactory;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistryImpl;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksCallableAdapter;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksCallableAdapterResult;

public final class ImmediatelyTasksCallableAdapter implements TasksCallableAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ImmediatelyTasksCallableAdapter.class);

	private final ImmediatelyTask task;

	@SuppressWarnings("unused")
	private final Configuration configuration;

	private final TaskMetadataRegistry taskMetadataRegistry;

	public ImmediatelyTasksCallableAdapter(Configuration configuration, ImmediatelyTaskCallable callable) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.taskMetadataRegistry = new TaskMetadataRegistryImpl(Constants.AD_SERVICE_TASK_METADATA_FILE_NAME);

		// @@@@@ taskId는 랜덤생성, taskDescription(정보 있어야됨, 아니면 call 함수를 수정, ImmediatelyTaskCallable 객체에서 받는다??)
		// Task를 생성한다.
		String description = callable.getDescription();
		this.task = (ImmediatelyTask) TaskFactory.createTask(TaskType.IMMEDIATELY, "taskId", description, taskMetadataRegistry);
	}

	@Override
	public TasksCallableAdapterResult call() throws Exception {
		try {
			logger.debug("Task를 실행합니다.(Task:{})", this.task.getTaskDescription());

			try {
				TaskResult taskResult = this.task.run();
				if (taskResult != TaskResult.OK) {
					logger.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, this.task.getTaskDescription());
					return TasksCallableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
				} else {
					logger.debug("Task 실행이 완료되었습니다.(Task:{})", this.task.getTaskDescription());
					return TasksCallableAdapterResult.OK(TaskResult.OK);
				}
			} catch (Throwable e) {
				logger.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", this.task.getTaskDescription(), e);
				return TasksCallableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
			}
		} catch (Exception e) {
			logger.error(null, e);
		}

		return TasksCallableAdapterResult.UNEXPECTED_EXCEPTION();
	}

}
