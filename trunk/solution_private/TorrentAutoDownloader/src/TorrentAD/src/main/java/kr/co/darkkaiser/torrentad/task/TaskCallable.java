package kr.co.darkkaiser.torrentad.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public final class TaskCallable implements Callable<TaskResult> {

	private static final Logger logger = LoggerFactory.getLogger(TaskCallable.class);
	
	private final List<Task> tasks = new ArrayList<>();

	public TaskCallable() {
	}
	
	public void init(ConfigurationManager configurationManager) {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}

		//@@@@@
	}

	@Override
	public TaskResult call() throws Exception {
		// 사이트 로그인
		WebSiteHandler l = new BogoBogoWebSite();

		// 사이트 로그인을 다음과 같이 변경
//		WebSiteHandler l2 = WebSiteSupport.BOGOBOGO("darkkaiser", "DreamWakuWaku78@");
		try {
			l.login(new BogoBogoWebSiteAccount("darkkaiser", "DreamWakuWaku78@"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TaskResult resultValue = null;
		for (Task task : this.tasks) {
			try {
				resultValue = task.execute(l);
			} catch (Exception e) {
				// @@@@@ 메시지 추가
				logger.error(null, e);
			}
		}
		
		// 사이트 로그아웃
		try {
			l.logout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return TaskResult.OK;
	}

}
