package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskFactory;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksCallableAdapter;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksCallableAdapterResult;
import kr.co.darkkaiser.torrentad.util.metadata.repository.MetadataRepositoryImpl;

public final class ImmediatelyTasksCallableAdapter implements TasksCallableAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ImmediatelyTasksCallableAdapter.class);

	private final ImmediatelyTask task;
	
	private static AtomicInteger count = new AtomicInteger(0);

	public ImmediatelyTasksCallableAdapter(Configuration configuration, ImmediatelyTaskAction action) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");
		if (action == null)
			throw new NullPointerException("action");

		String name = action.getName();
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("ImmediatelyTaskAction의 name은 빈 문자열을 허용하지 않습니다.");

		MetadataRepositoryImpl metadataRepository = new MetadataRepositoryImpl(Constants.METADATA_REPOSITORY_FILE_NAME);
		this.task = ((ImmediatelyTask) TaskFactory.createTask(TaskType.IMMEDIATELY, String.format("ImmediatelyTask_%05d", count.incrementAndGet()), name, metadataRepository)).setAction(action);
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
