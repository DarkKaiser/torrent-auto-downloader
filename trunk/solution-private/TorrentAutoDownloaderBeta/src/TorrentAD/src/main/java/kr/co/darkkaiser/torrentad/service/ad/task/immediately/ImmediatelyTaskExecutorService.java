package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

public interface ImmediatelyTaskExecutorService {

	boolean submit(ImmediatelyTaskAction action);

}
