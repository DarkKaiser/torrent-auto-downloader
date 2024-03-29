package com.darkkaiser.torrentad.service.ad;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.Service;
import com.darkkaiser.torrentad.service.ad.task.TasksCallableAdapter;
import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskAction;
import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTasksCallableAdapter;
import com.darkkaiser.torrentad.service.ad.task.scheduled.ScheduledTasksCallableAdapter;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepositoryImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class TorrentAdService implements Service, ImmediatelyTaskExecutorService {

	private Timer scheduledTasksExecutorTimer;
	private ExecutorService scheduledTasksExecutorService;
	private TasksCallableAdapter scheduledTasksCallableAdapter;

	private ExecutorService immediatelyTasksExecutorService;

	private final Configuration configuration;
	
	private final MetadataRepository metadataRepository;

	public TorrentAdService(final Configuration configuration) {
		Objects.requireNonNull(configuration, "configuration");

		this.configuration = configuration;
		this.metadataRepository = new MetadataRepositoryImpl(Constants.AD_SERVICE_METADATA_REPOSITORY_FILE_NAME);
	}

	@Override
	public boolean start() throws Exception {
		if (this.scheduledTasksExecutorTimer != null)
			throw new IllegalStateException("scheduledTasksExecutorTimer 객체는 이미 초기화되었습니다.");
		if (this.scheduledTasksExecutorService != null)
			throw new IllegalStateException("scheduledTasksExecutorService 객체는 이미 초기화되었습니다");
		if (this.scheduledTasksCallableAdapter != null)
			throw new IllegalStateException("scheduledTasksCallableAdapter 객체는 이미 초기화되었습니다");
		if (this.immediatelyTasksExecutorService != null)
			throw new IllegalStateException("immediatelyTasksExecutorService 객체는 이미 초기화되었습니다");

		this.scheduledTasksExecutorService = Executors.newFixedThreadPool(1);
		this.scheduledTasksCallableAdapter = new ScheduledTasksCallableAdapter(this.configuration, this.metadataRepository);

		this.scheduledTasksExecutorTimer = new Timer();
		this.scheduledTasksExecutorTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TorrentAdService.this.scheduledTasksExecutorService.submit(TorrentAdService.this.scheduledTasksCallableAdapter);
			}
		}, 500, Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_TASK_EXECUTE_INTERVAL_TIME_SECOND)) * 1000L);

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
		this.scheduledTasksCallableAdapter = null;
		this.immediatelyTasksExecutorService = null;
	}

	@Override
	public boolean submit(final ImmediatelyTaskAction action) {
		if (this.immediatelyTasksExecutorService == null) {
			log.error("ImmediatelyTasksExecutorService가 중지된 상태에서 submit이 요청되었습니다.");
			return false;
		}

		try {
			this.immediatelyTasksExecutorService.submit(new ImmediatelyTasksCallableAdapter(this.configuration, this.metadataRepository, action));
		} catch (final Exception e) {
			log.error("ImmediatelyTaskAction의 작업 요청이 실패하였습니다.", e);
			return false;
		}

		return true;
	}

}
