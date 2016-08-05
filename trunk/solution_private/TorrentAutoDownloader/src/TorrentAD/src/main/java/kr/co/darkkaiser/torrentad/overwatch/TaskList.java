package kr.co.darkkaiser.torrentad.overwatch;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class TaskList implements Callable<Integer> {

	private ArrayList<Task> jobList = new ArrayList<>();

	@Override
	public Integer call() throws Exception {
		for (int index = 0; index < this.jobList.size(); ++index) {
			// 작업수행
		}
		
		return null;
	}

}
