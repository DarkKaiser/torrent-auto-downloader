package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import java.util.concurrent.Callable;

public interface ImmediatelyTaskAction extends Callable<Boolean> {

	String getName();

	void validate();

	boolean isValid();

}
