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

public final class TasksRunnableAdapter implements Callable<TasksExecutorServiceResultAdapter> {

	private static final Logger logger = LoggerFactory.getLogger(TasksRunnableAdapter.class);

	private final WebSite site;
	private final String siteLoginId;
	private final String siteLoginPassword;

	private final List<Task> tasks = new ArrayList<>();

	private final ConfigurationManager configurationManager;

	private final AES256Util aes256;

	public TasksRunnableAdapter(AES256Util aes256, ConfigurationManager configurationManager) throws Exception {
		if (aes256 == null) {
			throw new NullPointerException("aes256");
		}
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}

		this.aes256 = aes256;
		this.configurationManager = configurationManager;

		// 토렌트 사이트 이름을 구한다.
		try {
			this.site = WebSite.fromString(this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_NAME));
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 이름('{}')이 유효하지 않습니다.", Constants.APP_CONFIG_KEY_WEBSITE_NAME);
			throw e;
		}

		// 토렌트 사이트 계정 정보를 구한다.
		this.siteLoginId = this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_ID);
		String encryptionPassword = this.configurationManager.getValue(Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD);

		try {
			this.siteLoginPassword = this.aes256.decode(encryptionPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 비밀번호('{}')의 복호화 작업이 실패하였습니다.", Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD);
			throw e;
		}

		TaskGenerator.load(this.configurationManager, this.site, this.tasks);
	}

	@Override
	public TasksExecutorServiceResultAdapter call() throws Exception {
		logger.info("새 토렌트 파일 확인 작업을 시작합니다.");

		TasksExecutorServiceResultAdapter result = call0();

		logger.info("새 토렌트 파일 확인 작업이 종료되었습니다.");
		
		return result;
	}

	private TasksExecutorServiceResultAdapter call0() throws Exception {
		WebSiteAccount account = null;
		try {
			account = this.site.createAccount(this.siteLoginId, this.siteLoginPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 계정정보({})가 유효하지 않습니다.", String.format("'%s', '%s'", Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_ID, Constants.APP_CONFIG_KEY_WEBSITE_ACCOUNT_PASSWORD), e);
			return TasksExecutorServiceResultAdapter.INVALID_ACCOUNT();
		}

		WebSiteHandler handler = this.site.createHandler();

		try {
			handler.login(account);
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그인이 실패하였습니다.", this.site, e);
			return TasksExecutorServiceResultAdapter.WEBSITE_LOGIN_FAILED();
		}

		// 모든 Task의 실행 결과가 성공이면 반환값은 OK를 반환한다.
		// 하지만 하나 이상의 Task 실행이 실패하면, 마지막 실패한 Task의 실패코드를 반환한다.
		TasksExecutorServiceResultAdapter result = TasksExecutorServiceResultAdapter.OK();

		for (Task task : this.tasks) {
			logger.debug("Task 실행:{}", task);

			try {
				TaskResult taskResult = task.run(handler);
				if (taskResult != TaskResult.OK) {
					logger.error("Task 실행이 실패('{}') 하였습니다.", taskResult);
					result = TasksExecutorServiceResultAdapter.TASK_EXECUTION_FAILED(taskResult);
				} else {
					logger.debug("Task 실행이 완료되었습니다.");
				}
			} catch (Exception e) {
				logger.error("Task 실행 중 예외가 발생하였습니다.", e);
				result = TasksExecutorServiceResultAdapter.UNEXPECTED_TASK_RUNNING_EXCEPTION();
			}
		}

		handler.logout();

		return result;
	}

}
