package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import java.util.concurrent.Callable;

import kr.co.darkkaiser.torrentad.service.ad.task.Task;
import kr.co.darkkaiser.torrentad.service.ad.task.TaskResult;

public interface ImmediatelyTask extends Task, Callable<TaskResult> {

}
