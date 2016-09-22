package kr.co.darkkaiser.torrentad.service.au;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.service.au.action.ActionFactory;
import kr.co.darkkaiser.torrentad.service.au.action.ActionType;
import kr.co.darkkaiser.torrentad.service.au.action.transmission.FileTransmissionAction;

public class TorrentAuService implements Service {

	private File fileWatchLocation;

	private Timer fileWatcherTimer;

	private Timer torrentSupervisoryControlTimer;

	private ExecutorService actionsExecutorService;

	private final Configuration configuration;

	public TorrentAuService(Configuration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
	}
	
	@Override
	public boolean start() throws Exception {
		if (this.fileWatchLocation != null)
			throw new IllegalStateException("fileWatchLocation 객체는 이미 초기화되었습니다.");
		if (this.fileWatcherTimer != null)
			throw new IllegalStateException("fileWatcherTimer 객체는 이미 초기화되었습니다.");
		if (this.torrentSupervisoryControlTimer != null)
			throw new IllegalStateException("torrentSupervisoryControlTimer 객체는 이미 초기화되었습니다.");
		if (this.actionsExecutorService != null)
			throw new IllegalStateException("actionExecutorService 객체는 이미 초기화되었습니다");
		if (this.configuration == null)
			throw new NullPointerException("configuration");

		this.fileWatcherTimer = new Timer();
		this.torrentSupervisoryControlTimer = new Timer();
		this.actionsExecutorService = Executors.newFixedThreadPool(1);

		// 파일에 대한 변경을 감시 할 경로를 구한다.
		this.fileWatchLocation = new File(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION));
		if (this.fileWatchLocation.exists() == false) {
			throw new FileNotFoundException(this.fileWatchLocation.getAbsolutePath());
		}

		// 파일에 대한 변경을 감시하는 타이머를 시작한다.
		this.fileWatcherTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				File[] listFiles = TorrentAuService.this.fileWatchLocation.listFiles(new FilenameFilter() {
				    @Override
				    public boolean accept(File dir, String name) {
				        return name.toLowerCase().endsWith(Constants.AD_SERVICE_TASK_NOTYET_DOWNLOADED_FILE_EXTENSION) == false;
				    }
				});

				FileTransmissionAction action = (FileTransmissionAction) ActionFactory.createAction(ActionType.FILE_TRANSMISSION, TorrentAuService.this.configuration);

				for (File file : listFiles) {
					if (file.isFile() == true)
						action.addFile(file);
				}

				if (action.getFileCount() > 0)
					TorrentAuService.this.actionsExecutorService.submit(action);
			}
		}, 1000, Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WATCH_INTERVAL_TIME_SECOND)) * 1000);

		// 토렌트의 상태를 감시 및 제어하는 Action을 발생시키는 타이머를 시작한다.
		this.torrentSupervisoryControlTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TorrentAuService.this.actionsExecutorService.submit(ActionFactory.createAction(ActionType.TORRENT_SUPERVISORY_CONTROL, TorrentAuService.this.configuration));
			}
		}, 1000, Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_SUPERVISORY_CONTROL_INTERVAL_TIME_SECOND)) * 1000);
		return true;
	}

	@Override
	public void stop() {
		if (this.fileWatcherTimer != null)
			this.fileWatcherTimer.cancel();
		if (this.torrentSupervisoryControlTimer != null)
			this.torrentSupervisoryControlTimer.cancel();
		if (this.actionsExecutorService != null)
			this.actionsExecutorService.shutdown();

		this.fileWatcherTimer = null;
		this.actionsExecutorService = null;
		this.torrentSupervisoryControlTimer = null;
		this.fileWatchLocation = null;
	}

}
