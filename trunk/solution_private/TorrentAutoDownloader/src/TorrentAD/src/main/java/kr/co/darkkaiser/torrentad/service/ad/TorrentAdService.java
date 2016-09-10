package kr.co.darkkaiser.torrentad.service.ad;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksRunnableAdapter;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public final class TorrentAdService implements Service {

	private Timer tasksExecutorTimer;

	private ExecutorService tasksExecutorService;

	private TasksRunnableAdapter tasksRunnableAdapter;

	private final ConfigurationManager configurationManager;

	private final AES256Util aes256;

	public TorrentAdService(AES256Util aes256, ConfigurationManager configurationManager) throws UnsupportedEncodingException {
		if (aes256 == null) {
			throw new NullPointerException("aes256");
		}
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}

		this.aes256 = aes256;
		this.configurationManager = configurationManager;
	}

	@Override
	public boolean start() throws Exception {
		if (this.tasksExecutorTimer != null) {
			throw new IllegalStateException("tasksExecutorTimer 객체는 이미 초기화되었습니다.");
		}
		if (this.tasksExecutorService != null) {
			throw new IllegalStateException("tasksExecutorService 객체는 이미 초기화되었습니다");
		}
		if (this.tasksRunnableAdapter != null) {
			throw new IllegalStateException("tasksRunnableAdapter 객체는 이미 초기화되었습니다");
		}
		if (this.configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		
		this.tasksExecutorTimer = new Timer();
		this.tasksExecutorService = Executors.newFixedThreadPool(1);
		this.tasksRunnableAdapter = new TasksRunnableAdapter(this.aes256, this.configurationManager);
		
		this.tasksExecutorTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TorrentAdService.this.tasksExecutorService.submit(TorrentAdService.this.tasksRunnableAdapter);
			}
		}, 500, Integer.parseInt(this.configurationManager.getValue(Constants.APP_CONFIG_TAG_TASK_EXECUTE_INTERVAL_TIME_SECOND)) * 1000);

		return true;
	}

	@Override
	public void stop() {
		if (this.tasksExecutorTimer != null) {
			this.tasksExecutorTimer.cancel();
		}
		
		if (this.tasksExecutorService != null) {
			this.tasksExecutorService.shutdown();
		}
		
		this.tasksExecutorTimer = null;
		this.tasksExecutorService = null;
		this.tasksRunnableAdapter = null;
	}
	
}