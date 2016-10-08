package kr.co.darkkaiser.torrentad.service.ad;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.service.ad.task.ScheduledTasksRunnableAdapter;

public final class TorrentAdService implements Service {

	private Timer scheduledTasksExecutorTimer;
	private ExecutorService scheduledTasksExecutorService;
	private ScheduledTasksRunnableAdapter scheduledTasksRunnableAdapter;

	private ExecutorService immediatelyTasksExecutorService;

	private final Configuration configuration;

	public TorrentAdService(Configuration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
	}

	@Override
	public boolean start() throws Exception {
		if (this.scheduledTasksExecutorTimer != null)
			throw new IllegalStateException("scheduledTasksExecutorTimer 객체는 이미 초기화되었습니다.");
		if (this.scheduledTasksExecutorService != null)
			throw new IllegalStateException("scheduledTasksExecutorService 객체는 이미 초기화되었습니다");
		if (this.scheduledTasksRunnableAdapter != null)
			throw new IllegalStateException("scheduledTasksRunnableAdapter 객체는 이미 초기화되었습니다");
		if (this.immediatelyTasksExecutorService != null)
			throw new IllegalStateException("immediatelyTasksExecutorService 객체는 이미 초기화되었습니다");
		if (this.configuration == null)
			throw new NullPointerException("configuration");
		
		this.scheduledTasksExecutorTimer = new Timer();
		this.scheduledTasksExecutorService = Executors.newFixedThreadPool(1);
		this.scheduledTasksRunnableAdapter = new ScheduledTasksRunnableAdapter(this.configuration);
		
		this.scheduledTasksExecutorTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TorrentAdService.this.scheduledTasksExecutorService.submit(TorrentAdService.this.scheduledTasksRunnableAdapter);
			}
		}, 500, Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_TASK_EXECUTE_INTERVAL_TIME_SECOND)) * 1000);

		this.immediatelyTasksExecutorService = Executors.newFixedThreadPool(1);

		return true;
	}

	@Override
	public void stop() {
		if (this.scheduledTasksExecutorTimer != null)
			this.scheduledTasksExecutorTimer.cancel();
		
		if (this.scheduledTasksExecutorService != null)
			this.scheduledTasksExecutorService.shutdown();

		if (this.immediatelyTasksExecutorService != null)
			this.immediatelyTasksExecutorService.shutdown();
		
		this.scheduledTasksExecutorTimer = null;
		this.scheduledTasksExecutorService = null;
		this.scheduledTasksRunnableAdapter = null;
		this.immediatelyTasksExecutorService = null;
	}

}
