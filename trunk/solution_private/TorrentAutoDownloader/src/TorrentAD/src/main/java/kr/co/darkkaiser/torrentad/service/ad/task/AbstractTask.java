package kr.co.darkkaiser.torrentad.service.ad.task;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTask implements Task {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTask.class);

	protected final TaskType taskType;
	
	protected final String taskId;
	protected final String taskDescription;
	
	protected final TaskMetadataRegistry taskMetadataRegistry;
	
	protected AbstractTask(TaskType taskType, String taskId, String taskDescription, TaskMetadataRegistry taskMetadataRegistry) {
		if (taskType == null)
			throw new NullPointerException("taskType");
		if (StringUtil.isBlank(taskId) == true)
			throw new IllegalArgumentException("taskId는 빈 문자열을 허용하지 않습니다.");
		if (taskMetadataRegistry == null)
			throw new NullPointerException("taskMetadataRegistry");

		this.taskId = taskId;
		this.taskType = taskType;
		this.taskMetadataRegistry = taskMetadataRegistry;

		if (StringUtil.isBlank(taskDescription) == false) {
			this.taskDescription = taskDescription;
		} else {
			this.taskDescription = taskId;
		}
	}

	@Override
	public TaskType getTaskType() {
		return this.taskType;
	}
	
	@Override
	public String getTaskId() {
		return this.taskId;
	}
	
	@Override
	public String getTaskDescription() {
		return this.taskDescription;
	}

	@Override
	public TaskMetadataRegistry getTaskMetadataRegistry() {
		return this.taskMetadataRegistry;
	}

	@Override
	public void validate() {
		if (this.taskType == null)
			throw new NullPointerException("taskType");
		if (StringUtil.isBlank(this.taskId) == true)
			throw new IllegalArgumentException("taskId는 빈 문자열을 허용하지 않습니다.");
		if (this.taskMetadataRegistry == null)
			throw new NullPointerException("taskMetadataRegistry");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			logger.debug(null, e);
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractTask.class.getSimpleName())
				.append("{")
				.append("taskType:").append(this.taskType)
				.append(", taskId:").append(this.taskId)
				.append(", taskDescription:").append(this.taskDescription)
				.append("}")
				.toString();
	}
	
}
