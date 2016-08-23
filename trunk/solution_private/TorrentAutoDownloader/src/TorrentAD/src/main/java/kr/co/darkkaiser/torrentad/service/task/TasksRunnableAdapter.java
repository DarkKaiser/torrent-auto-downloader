package kr.co.darkkaiser.torrentad.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public final class TasksRunnableAdapter implements Callable<TaskResult> {

	private static final Logger logger = LoggerFactory.getLogger(TasksRunnableAdapter.class);

	private final List<Task> tasks = new ArrayList<>();

	private ConfigurationManager configurationManager;

	public TasksRunnableAdapter(ConfigurationManager configurationManager) {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		if (this.configurationManager != null) {
			throw new IllegalStateException("configurationManager set already");
		}

		this.configurationManager = configurationManager;

		init(configurationManager);
	}

	private void init(ConfigurationManager configurationManager) {
		//@@@@@ 환경설정정보 로드해서 task 초기화, taskfactory 이용
	}

	@Override
	public TaskResult call() throws Exception {
		WebSiteHandler handler = new BogoBogoWebSite();

		// 사이트 로그인을 다음과 같이 변경
		try {
			handler.login(new BogoBogoWebSiteAccount("darkkaiser", "DreamWakuWaku78@"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		TaskResult resultValue = null;
		for (Task task : this.tasks) {
			try {
				resultValue = task.run(handler);
			} catch (Exception e) {
				// @@@@@ 메시지 추가
				logger.error(null, e);
			}
		}
		
		// 사이트 로그아웃
		try {
			handler.logout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return TaskResult.OK;
	}

}
