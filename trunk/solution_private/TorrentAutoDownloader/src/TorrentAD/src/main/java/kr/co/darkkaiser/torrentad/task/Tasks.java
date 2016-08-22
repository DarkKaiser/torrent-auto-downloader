package kr.co.darkkaiser.torrentad.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;

public final class Tasks implements Callable<Integer> {

	private static final Logger logger = LoggerFactory.getLogger(Tasks.class);
	
	private final List<Task> tasks = new ArrayList<>();

	public Tasks() {
	}
	
	public void load(ConfigurationManager configurationManager) {
		//@@@@@
	}

	@Override
	public Integer call() throws Exception {
		// 사이트 로그인
		BogoBogoWebSite l = new BogoBogoWebSite();

		try {
			l.login(new BogoBogoWebSiteAccount("darkkaiser", "DreamWakuWaku78@"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Integer resultValue = null;
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
		
		return resultValue;
	}

}
