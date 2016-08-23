package kr.co.darkkaiser.torrentad.service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.service.task.TasksExecutableAdapter;

public final class TorrentADService {

	private static final Logger logger = LoggerFactory.getLogger(TorrentADService.class);

	private Timer timer;

	private ExecutorService taskService;

	private ConfigurationManager configurationManager;

	// @@@@@
	private TasksExecutableAdapter taskManager = new TasksExecutableAdapter();

	public TorrentADService(ConfigurationManager configurationManager) {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		if (this.configurationManager != null) {
			throw new IllegalStateException("configurationManager set already");
		}

		this.configurationManager = configurationManager;
	}

	public boolean start() {
		// @@@@@
		if (this.timer != null) {// @@@@@ 변수명
			throw new IllegalStateException("timer set already");
		}
		if (this.taskService != null) {
			throw new IllegalStateException("torrentADService set already");
		}
		if (this.configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		
		// 작업 정보를 읽어온다.
		// @@@@@
		this.taskManager.init(this.configurationManager);
		
		this.taskService = Executors.newFixedThreadPool(1);

		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// @@@@@ 시간 및 처리자
				TorrentADService.this.taskService.submit(TorrentADService.this.taskManager);
			}
		}, 10, 10000);

		return true;
	}

	public void stop() {
		// @@@@@
		if (this.timer != null) {
			this.timer.cancel();
		}
		if (this.taskService != null) {
			this.taskService.shutdown();// @@@@@ shutdown() or shutdownNow() 선택
		}
		
		this.timer = null;
		this.taskService = null;
	}
	
}
