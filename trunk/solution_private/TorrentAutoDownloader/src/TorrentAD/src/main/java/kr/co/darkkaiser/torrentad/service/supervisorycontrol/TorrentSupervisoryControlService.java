package kr.co.darkkaiser.torrentad.service.supervisorycontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class TorrentSupervisoryControlService implements Service {

	private static final Logger logger = LoggerFactory.getLogger(TorrentSupervisoryControlService.class);

	private File fileWatchLocation;

	private Timer fileWatcherTimer;

	// @@@@@ 토렌트 watch하는 타이머를 만들어서 actionsExecutorService에 주기적으로 계속 추가

	private ExecutorService actionsExecutorService;

	private final Configuration configuration;

	private final AES256Util aes256;

	public TorrentSupervisoryControlService(AES256Util aes256, Configuration configuration) throws UnsupportedEncodingException {
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
		if (this.fileWatcherTimer != null) {
			throw new IllegalStateException("fileWatcherTimer 객체는 이미 초기화되었습니다.");
		}
		if (this.actionsExecutorService != null) {
			throw new IllegalStateException("actionExecutorService 객체는 이미 초기화되었습니다");
		}
		if (this.configuration == null) {
			throw new NullPointerException("configuration");
		}

		this.fileWatcherTimer = new Timer();
		this.actionsExecutorService = Executors.newFixedThreadPool(1);

		// @@@@@
//		this.actionsExecutorService.submit(JOB);

		// 파일에 대한 변경을 감시 할 경로를 구한다.
		this.fileWatchLocation = new File(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION));
		if (this.fileWatchLocation.exists() == false) {
			throw new FileNotFoundException(this.fileWatchLocation.getAbsolutePath());
		}

		// 파일에 대한 변경을 감시하는 타이머를 시작한다.
		this.fileWatcherTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				File[] listFiles = TorrentSupervisoryControlService.this.fileWatchLocation.listFiles(new FilenameFilter() {
				    @Override
				    public boolean accept(File dir, String name) {
				        return !name.toLowerCase().endsWith(Constants.AD_SERVICE_TASK_DOWNLOADING_FILE_EXTENSION);
				    }
				});

				for (File file : listFiles) {
					if (file.isDirectory() == false) {
						System.out.println(file.getAbsolutePath());
						// 파일 목록에 따라서 액션익스큐터서비스에 추가
						// @@@@@ 이전에 생성된 파일들은 수동으로 추가해줘야됨, 누락되지않도록 해야됨
					}
				}
			}
		}, 1000, Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WATCH_INTERVAL_TIME_SECOND)) * 1000);

		return true;
	}

	@Override
	public void stop() {
		if (this.fileWatcherTimer != null) {
			this.fileWatcherTimer.cancel();
		}
		if (this.actionsExecutorService != null) {
			this.actionsExecutorService.shutdown();
		}

		this.fileWatcherTimer = null;
		this.fileWatchLocation = null;
		this.actionsExecutorService = null;
	}

}
