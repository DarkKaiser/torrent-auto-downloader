package kr.co.darkkaiser.torrentad.overwatch;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class JobList implements Callable<Integer> {
	
	private ArrayList<Job> jobList = new ArrayList<>();

	@Override
	public Integer call() throws Exception {
		for (int index = 0; index < this.jobList.size(); ++index) {
			// 작업수행
		}
		
		return null;
	}

}
