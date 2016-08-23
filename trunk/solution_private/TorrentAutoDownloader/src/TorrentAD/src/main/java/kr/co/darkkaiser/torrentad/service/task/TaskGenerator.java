package kr.co.darkkaiser.torrentad.service.task;

import java.util.List;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;

public class TaskGenerator {

	public static void generate(List<Task> tasks, ConfigurationManager configurationManager) {
		// TODO Auto-generated method stub
		// @@@@@
		tasks.add(new RegularTaskImpl());
	}

}
