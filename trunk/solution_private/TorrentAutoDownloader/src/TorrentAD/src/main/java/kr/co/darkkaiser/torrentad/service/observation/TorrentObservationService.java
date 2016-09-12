package kr.co.darkkaiser.torrentad.service.observation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class TorrentObservationService implements Service {

	private static final Logger logger = LoggerFactory.getLogger(TorrentObservationService.class);

	private WatchService watchService;

	private ExecutorService watchExecutorService;

	// @@@@@ 변수명(task)
	private ExecutorService tasksExecutorService;
		
	private final Configuration configuration;

	private final AES256Util aes256;

	public TorrentObservationService(AES256Util aes256, Configuration configuration) throws UnsupportedEncodingException {
		if (aes256 == null) {
			throw new NullPointerException("aes256");
		}
		if (configuration == null) {
			throw new NullPointerException("configuration");
		}

		this.aes256 = aes256;
		this.configuration = configuration;
	}
	
	@Override
	public boolean start() throws Exception {
		if (this.watchService != null) {
			throw new IllegalStateException("watchService 객체는 이미 초기화되었습니다");
		}
		if (this.watchExecutorService != null) {
			throw new IllegalStateException("watchExecutorService 객체는 이미 초기화되었습니다");
		}
		if (this.tasksExecutorService != null) {
			throw new IllegalStateException("tasksExecutorService 객체는 이미 초기화되었습니다");
		}
		if (this.configuration == null) {
			throw new NullPointerException("configuration");
		}

		this.watchService = FileSystems.getDefault().newWatchService();
		this.watchExecutorService = Executors.newCachedThreadPool();		
		this.tasksExecutorService = Executors.newFixedThreadPool(1);

		// @@@@@
//		this.tasksExecutorService.submit(JOB);

		// 파일에 대한 변경을 감시 할 경로를 등록한다.
		try {
			// @@@@@
			String value = this.configuration.getValue("watch-path");
			Path watchPath = Paths.get("test");
			watchPath.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (NoSuchFileException e) {
			logger.error("파일에 대한 변경을 감시 할 경로를 등록하는 도중에 예외가 발생하였습니다.", e);

			stop();

			return false;
		}

		// 파일에 대한 변경을 감시하는 서비스를 시작한다.
		this.watchExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				// @@@@@
				System.out.println("################## 1");
			}
		});

		return true;
	}

	@Override
	public void stop() {
		if (this.watchService != null) {
			try {
				this.watchService.close();
			} catch (IOException e) {
				logger.error(null, e);
			}
		}
		if (this.watchExecutorService != null) {
			this.watchExecutorService.shutdownNow();
		}
		if (this.tasksExecutorService != null) {
			this.tasksExecutorService.shutdown();
		}

		this.watchService = null;
		this.watchExecutorService = null;
		this.tasksExecutorService = null;
	}

}
