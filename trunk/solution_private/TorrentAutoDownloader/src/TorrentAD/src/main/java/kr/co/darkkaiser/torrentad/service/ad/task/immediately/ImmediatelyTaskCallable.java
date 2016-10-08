package kr.co.darkkaiser.torrentad.service.ad.task.immediately;

import java.util.concurrent.Callable;

// @@@@@ 인터페이스명
public interface ImmediatelyTaskCallable extends Callable<Boolean> {

	String getDescription();

	void validate();

	boolean isValid();

}
