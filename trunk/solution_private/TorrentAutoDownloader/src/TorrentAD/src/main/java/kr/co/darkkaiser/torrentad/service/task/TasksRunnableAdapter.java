package kr.co.darkkaiser.torrentad.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.website.WebSite;
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
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}

		this.aes256 = aes256;
		this.configurationManager = configurationManager;

		init(configurationManager);
	}

	private void init(ConfigurationManager configurationManager) {
		assert configurationManager != null;
		
		//@@@@@ 환경설정정보 로드해서 task 초기화, taskfactory 이용
		TaskGenerator.generate(this.tasks, this.configurationManager);
	}

	@Override
	public TaskResult call() throws Exception {
		logger.info("새 토렌트 파일 확인 작업을 시작합니다.");

		TaskResult taskResult = call0();

		logger.info("새 토렌트 파일 확인 작업이 종료되었습니다.");
		
		return taskResult;
	}

	private TaskResult call0() throws Exception {
		String id = this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_ID);
		String password = this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD);
		try {
			password = this.aes256.aesDecode(password);
		} catch (Exception e) {
			logger.error("등록된 비밀번호의 복호화 작업이 실패하였습니다.", e);
			return TaskResult.FAILED_DECODE_PASSWORD;
		}
		
		// @@@@@ 설정파일에서 읽어와서 할당
//		System.out.println(this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_NAME));
		WebSite site = WebSite.get(this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_NAME));
//		System.out.println(site);
//		return null;

		WebSiteAccount account = null;
		try {
			account = site.createAccount(id, password);
		} catch (Exception e) {
			logger.error("등록된 계정 정보가 유효하지 않습니다.", e);
			return TaskResult.INVALID_ACCOUNT;
		}

		WebSiteHandler handler = site.createHandler();

		try {
			handler.login(account);
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그인이 실패하였습니다.", site, e);
			return TaskResult.FAILED_LOGIN;
		}

		TaskResult taskResult = TaskResult.OK;
		for (Task task : this.tasks) {
			try {
				taskResult = task.run(handler);
				// @@@@@ ok가 아니면???
				if (taskResult != TaskResult.OK) {
//					logger.warn("Task 실행 중 예외가 발생하였습니다.", e);
				}
			} catch (Exception e) {
				taskResult = TaskResult.UNEXPECTED_TASK_RUNNING_EXCEPTION;
				logger.error("Task 실행 중 예외가 발생하였습니다.", e);
				break;
			}
		}

		handler.logout();

		return taskResult;
	}

}
