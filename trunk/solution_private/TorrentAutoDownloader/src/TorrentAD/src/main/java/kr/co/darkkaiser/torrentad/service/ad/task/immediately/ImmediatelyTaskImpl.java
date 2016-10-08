package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.ad.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskMetadataRegistry;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskType;

public class ImmediatelyTaskImpl extends AbstractTask implements ImmediatelyTask {

	private static final Logger logger = LoggerFactory.getLogger(ImmediatelyTaskImpl.class);
	
	// @@@@@ 변수명
	protected ImmediatelyTaskCallable callable;
	
	public ImmediatelyTaskImpl(String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry) {
		super(TaskType.IMMEDIATELY, taskId, taskDescription, taskMetadataRegistry);
	}

	@Override
	public TaskResult run() {
		validate();

		try {
			if (this.callable.call() == false)
				return TaskResult.FAILED;
		} catch (Exception e) {
			logger.error(null, e);
			return TaskResult.UNEXPECTED_EXCEPTION;
		}

		return TaskResult.OK;
	}

	@Override
	public void validate() {
		super.validate();

		// @@@@@ 변수명
		if (this.callable == null)
			throw new NullPointerException("callable");

		this.callable.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ImmediatelyTaskImpl.class.getSimpleName())
				.append("{")
				.append("callable:").append(this.callable)//@@@@@ 변수명
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
