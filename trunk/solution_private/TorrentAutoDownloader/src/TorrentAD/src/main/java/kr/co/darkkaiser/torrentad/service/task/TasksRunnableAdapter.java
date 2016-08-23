package kr.co.darkkaiser.torrentad.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.util.AES256Util;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public final class TasksRunnableAdapter implements Callable<TaskResult> {

	private static final Logger logger = LoggerFactory.getLogger(TasksRunnableAdapter.class);

	private final List<Task> tasks = new ArrayList<>();

	private final ConfigurationManager configurationManager;

	private final AES256Util aes256;

	public TasksRunnableAdapter(AES256Util aes256, ConfigurationManager configurationManager) {
		if (aes256 == null) {
			throw new NullPointerException("aes256");
		}
		if (this.aes256 != null) {
			throw new IllegalStateException("aes256 set already");
		}
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		if (this.configurationManager != null) {
			throw new IllegalStateException("configurationManager set already");
		}

		this.aes256 = aes256;
		this.configurationManager = configurationManager;

		init(configurationManager);
	}

	private void init(ConfigurationManager configurationManager) {
		//@@@@@ 환경설정정보 로드해서 task 초기화, taskfactory 이용
	}

	@Override
	public TaskResult call() throws Exception {
		WebSiteHandler handler = new BogoBogoWebSite();
		// @@@@@ 비밀번호 틀릴때 처리
		WebSiteAccount account = new BogoBogoWebSiteAccount(this.configurationManager.getValue("website-account-id"), this.aes256.aesDecode(this.configurationManager.getValue("website-account-password")));
		
		// 사이트 로그인을 다음과 같이 변경
		try {
			handler.login(account);
		} catch (Exception e) {
			logger.error(null, e);
			return TaskResult.OK;
		}

		TaskResult taskResult = TaskResult.OK;
		for (Task task : this.tasks) {
			try {
				taskResult = task.run(handler);
			} catch (Exception e) {
				// @@@@@ 메시지 추가
				logger.error(null, e);
				break;
			}
		}

		// 사이트 로그아웃
		try {
			handler.logout();
		} catch (Exception e) {
			logger.error(null, e);
			return TaskResult.OK;
		}
		
		return taskResult;
	}

}
