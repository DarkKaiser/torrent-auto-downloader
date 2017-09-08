package com.darkkaiser.torrentad.service.ad.task.immediately;

public interface ImmediatelyTaskExecutorService {

	boolean submit(final ImmediatelyTaskAction action);

}
