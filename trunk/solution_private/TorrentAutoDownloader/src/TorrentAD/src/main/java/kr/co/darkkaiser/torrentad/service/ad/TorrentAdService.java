package kr.co.darkkaiser.torrentad.service.ad;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.service.ad.task.TasksRunnableAdapter;

public final class TorrentAdService implements Service {

	private Timer scheduleTasksExecutorTimer;
	private ExecutorService scheduleTasksExecutorService;
	private TasksRunnableAdapter scheduleTasksRunnableAdapter;
	
	// @@@@@
	private ExecutorService immediatelyTasksExecutorService;

	private final Configuration configuration;

	public TorrentAdService(Configuration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
	}

	@Override
	public boolean start() throws Exception {
		if (this.scheduleTasksExecutorTimer != null)
			throw new IllegalStateException("scheduleTasksExecutorTimer 객체는 이미 초기화되었습니다.");
		if (this.scheduleTasksExecutorService != null)
			throw new IllegalStateException("scheduleTasksExecutorService 객체는 이미 초기화되었습니다");
		if (this.scheduleTasksRunnableAdapter != null)
			throw new IllegalStateException("scheduleTasksRunnableAdapter 객체는 이미 초기화되었습니다");
		if (this.configuration == null)
			throw new NullPointerException("configuration");
		
		this.scheduleTasksExecutorTimer = new Timer();
		this.scheduleTasksExecutorService = Executors.newFixedThreadPool(1);
		this.scheduleTasksRunnableAdapter = new TasksRunnableAdapter(this.configuration);

		this.scheduleTasksExecutorTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TorrentAdService.this.scheduleTasksExecutorService.submit(TorrentAdService.this.scheduleTasksRunnableAdapter);
			}
		}, 500, Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_TASK_EXECUTE_INTERVAL_TIME_SECOND)) * 1000);

		return true;
	}

	@Override
	public void stop() {
		if (this.scheduleTasksExecutorTimer != null)
			this.scheduleTasksExecutorTimer.cancel();
		
		if (this.scheduleTasksExecutorService != null)
			this.scheduleTasksExecutorService.shutdown();
		
		this.scheduleTasksExecutorTimer = null;
		this.scheduleTasksExecutorService = null;
		this.scheduleTasksRunnableAdapter = null;
	}
	
}
