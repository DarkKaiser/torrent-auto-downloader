package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import kr.co.darkkaiser.torrentad.service.ad.task.Task;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;

public interface ImmediatelyTask extends Task {

	TaskResult run();

}
