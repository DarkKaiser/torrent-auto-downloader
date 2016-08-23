package kr.co.darkkaiser.torrentad.service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.service.task.TasksRunnableAdapter;

public final class TorrentAdService {

	private Timer tasksExecutorTimer;

	private ExecutorService tasksExecutorService;

	private TasksRunnableAdapter tasksRunnableAdapter;

	private ConfigurationManager configurationManager;

	public TorrentAdService(ConfigurationManager configurationManager) {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		if (this.configurationManager != null) {
			throw new IllegalStateException("configurationManager set already");
		}

		this.configurationManager = configurationManager;
	}

	public boolean start() {
		if (this.tasksExecutorTimer != null) {
			throw new IllegalStateException("tasksExecutorTimer set already");
		}
		if (this.tasksExecutorService != null) {
			throw new IllegalStateException("tasksExecutorService set already");
		}
		if (this.tasksRunnableAdapter != null) {
			throw new IllegalStateException("tasksRunnableAdapter set already");
		}
		if (this.configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		
		this.tasksExecutorTimer = new Timer();
		this.tasksExecutorService = Executors.newFixedThreadPool(1);
		this.tasksRunnableAdapter = new TasksRunnableAdapter(this.configurationManager);
		
		this.tasksExecutorTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TorrentAdService.this.tasksExecutorService.submit(TorrentAdService.this.tasksRunnableAdapter);
			}
		}, 1000, Integer.parseInt(this.configurationManager.getValue(Constants.APP_CONFIG_KEY_TASK_EXECUTE_INTERVAL_TIME_SECOND)) * 1000);

		return true;
	}

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
