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
		// @@@@@ 결과 taskResult를 없애거나 TasksResult로 만드는건??
		
		String id = this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_ID);
		String password = this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD);
		try {
			password = this.aes256.aesDecode(password);
		} catch (Exception e) {
			logger.error("등록된 비밀번호('{}')의 복호화 작업이 실패하였습니다.", Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD, e);
			return TaskResult.FAILED_DECODE_PASSWORD;
		}

		WebSite site = null;
		try {
			site = WebSite.fromString(this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_NAME));
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 이름('{}')이 유효하지 않습니다.", Constants.APP_CONFIG_KEY_WEBSITE_NAME, e);
			return TaskResult.INVALID_WEBSITE_NAME;
		}

		WebSiteAccount account = null;
		try {
			account = site.createAccount(id, password);
		} catch (Exception e) {
			logger.error("등록된 로그인 계정정보({})가 유효하지 않습니다.", String.format("'%s', '%s'", Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_ID, Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD), e);
			return TaskResult.INVALID_ACCOUNT;
		}

		WebSiteHandler handler = site.createHandler();

		try {
			handler.login(account);
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그인이 실패하였습니다.", site, e);
			return TaskResult.FAILED_WEBSITE_LOGIN;
		}

		TaskResult taskResult = TaskResult.OK;
		for (Task task : this.tasks) {
			logger.debug("Task 실행:{}", task);

			try {
				taskResult = task.run(handler);
				// @@@@@ ok가 아니면???
				if (taskResult != TaskResult.OK) {
					logger.error("Task 실행이 실패('{}') 하였습니다.", taskResult);
				} else {
					logger.debug("Task 실행이 완료되었습니다.");
				}
			} catch (Exception e) {
				taskResult = TaskResult.UNEXPECTED_TASK_RUNNING_EXCEPTION;
				logger.error("Task 실행 중 예외가 발생하였습니다.", e);
			}
		}

		handler.logout();

		return taskResult;
	}

}
