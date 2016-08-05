package kr.co.darkkaiser.torrentad.overwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Tasks implements Callable<Integer> {

	private static final Logger logger = LoggerFactory.getLogger(Tasks.class);
	
	private final List<Task> tasks = new ArrayList<>();

	public Tasks() {
	}

	@Override
	public Integer call() throws Exception {
		Integer resultValue = null;
		for (Task task : this.tasks) {
			try {
				resultValue = task.call();
			} catch (Exception e) {
				// @@@@@ 메시지 추가
				logger.error(null, e);
			}
		}

		return resultValue;
	}

}
