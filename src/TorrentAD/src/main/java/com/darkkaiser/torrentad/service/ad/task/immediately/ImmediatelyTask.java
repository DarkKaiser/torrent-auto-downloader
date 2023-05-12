package com.darkkaiser.torrentad.service.ad.task.immediately;

import com.darkkaiser.torrentad.service.ad.task.Task;
import com.darkkaiser.torrentad.service.ad.task.TaskResult;

public interface ImmediatelyTask extends Task {
	
	ImmediatelyTask setAction(final ImmediatelyTaskAction action);

	TaskResult run();

}
