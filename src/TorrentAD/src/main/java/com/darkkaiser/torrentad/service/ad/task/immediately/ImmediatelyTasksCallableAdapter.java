package com.darkkaiser.torrentad.service.ad.task.immediately;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.ad.task.*;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class ImmediatelyTasksCallableAdapter implements TasksCallableAdapter {

	private final ImmediatelyTask task;
	
	private static final AtomicInteger count = new AtomicInteger(0);

	public ImmediatelyTasksCallableAdapter(final Configuration configuration, final MetadataRepository metadataRepository, final ImmediatelyTaskAction action) {
		Objects.requireNonNull(configuration, "configuration");
		Objects.requireNonNull(action, "action");

		String name = action.getName();
		if (StringUtil.isBlank(name) == true)
			throw new IllegalArgumentException("ImmediatelyTaskAction의 name은 빈 문자열을 허용하지 않습니다.");

		this.task = ((ImmediatelyTask) TaskFactory.createTask(TaskType.IMMEDIATELY, String.format("ImmediatelyTask_%05d", count.incrementAndGet()), name, metadataRepository)).setAction(action);
	}

	@Override
	public TasksCallableAdapterResult call() {
		try {
			log.debug("Task를 실행합니다.(Task:{})", this.task.getTaskDescription());

			try {
				TaskResult taskResult = this.task.run();
				if (taskResult != TaskResult.OK) {
					log.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, this.task.getTaskDescription());
					return TasksCallableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
				} else {
					log.debug("Task 실행이 완료되었습니다.(Task:{})", this.task.getTaskDescription());
					return TasksCallableAdapterResult.OK(TaskResult.OK);
				}
			} catch (final Throwable e) {
				log.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", this.task.getTaskDescription(), e);
				return TasksCallableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
			}
		} catch (final Exception e) {
			log.error(null, e);
		}

		return TasksCallableAdapterResult.UNEXPECTED_EXCEPTION();
	}

}
