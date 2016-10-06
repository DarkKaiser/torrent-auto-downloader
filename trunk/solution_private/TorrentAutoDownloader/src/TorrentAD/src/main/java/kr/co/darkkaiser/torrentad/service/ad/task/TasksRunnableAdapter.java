package kr.co.darkkaiser.torrentad.service.ad.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteConnection;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public final class TasksRunnableAdapter implements Callable<TasksRunnableAdapterResult> {

	private static final Logger logger = LoggerFactory.getLogger(TasksRunnableAdapter.class);

	private final WebSite site;

	private final String accountId;
	private final String accountPassword;

	private final List<Task> tasks;

	private final Configuration configuration;
	
	private final TaskMetadataRegistry taskMetadataRegistry;

	private final AES256Util aes256 = new AES256Util();

	public TasksRunnableAdapter(Configuration configuration) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.taskMetadataRegistry = new DefaultTaskMetadataRegistry(Constants.AD_SERVICE_TASK_METADATA_FILE_NAME);

		try {
			this.site = WebSite.fromString(this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_NAME));
		} catch (RuntimeException e) {
			logger.error("등록된 웹사이트의 이름('{}')이 유효하지 않습니다.", Constants.APP_CONFIG_TAG_WEBSITE_NAME);
			throw e;
		}

		this.accountId = this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID);
		String encryptionPassword = this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD);

		try {
			this.accountPassword = this.aes256.decode(encryptionPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 비밀번호('{}')의 복호화 작업이 실패하였습니다.", Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD);
			throw e;
		}

		// Task 목록을 생성한다.
		this.tasks = TaskGenerator.generate(this.configuration, this.taskMetadataRegistry, this.site);
	}

	@Override
	public TasksRunnableAdapterResult call() throws Exception {
		logger.info("새 토렌트 파일 확인을 시작합니다.");

		try {
			return call0();
		} catch (Exception e) {
			logger.error("새 토렌트 파일 확인중에 예외가 발생하였습니다.", e);
		} finally {
			logger.info("새 토렌트 파일 확인이 종료되었습니다.");
		}

		return TasksRunnableAdapterResult.UNEXPECTED_EXCEPTION();
	}

	private TasksRunnableAdapterResult call0() throws Exception {
		WebSiteAccount account = null;
		try {
			account = this.site.createAccount(this.accountId, this.accountPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 계정정보({})가 유효하지 않습니다.", String.format("'%s', '%s'", Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID, Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD), e);
			return TasksRunnableAdapterResult.INVALID_ACCOUNT();
		}

		WebSiteConnection connection = this.site.createConnection(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION));
		try {
			connection.login(account);
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그인이 실패하였습니다.", this.site, e);
			return TasksRunnableAdapterResult.WEBSITE_LOGIN_FAILED();
		}

		WebSiteHandler handler = (WebSiteHandler) connection;

		// 마지막으로 실행된 Task의 성공 또는 실패코드를 반환한다.
		TasksRunnableAdapterResult result = TasksRunnableAdapterResult.OK();

		for (Task task : this.tasks) {
			logger.debug("Task를 실행합니다.(Task:{})", task.getTaskDescription());

			try {
				TaskResult taskResult = task.run(handler);
				if (taskResult != TaskResult.OK) {
					logger.error("Task 실행이 실패('{}') 하였습니다.(Task:{})", taskResult, task.getTaskDescription());
					result = TasksRunnableAdapterResult.TASK_EXECUTION_FAILED(taskResult);
				} else {
					logger.debug("Task 실행이 완료되었습니다.(Task:{})", task.getTaskDescription());
					result = TasksRunnableAdapterResult.OK(TaskResult.OK);
				}
			} catch (Throwable e) {
				logger.error("Task 실행 중 예외가 발생하였습니다.(Task:{})", task.getTaskDescription(), e);
				result = TasksRunnableAdapterResult.UNEXPECTED_TASK_RUNNING_EXCEPTION();
			}
		}

		connection.logout();

		return result;
	}

}
