package kr.co.darkkaiser.torrentad.service.supervisorycontrol;

import java.io.File;
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

	private File downloadFileWriteLocation;
	
	// @@@@@ fileWatchExecutorTimer
	private Timer watchExecutorTimer;
	
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
		if (this.watchExecutorTimer != null) {
			throw new IllegalStateException("tasksExecutorTimer 객체는 이미 초기화되었습니다.");
		}
		if (this.actionsExecutorService != null) {
			throw new IllegalStateException("actionExecutorService 객체는 이미 초기화되었습니다");
		}
		if (this.configuration == null) {
			throw new NullPointerException("configuration");
		}

		this.watchExecutorTimer = new Timer();
		this.actionsExecutorService = Executors.newFixedThreadPool(1);

		// @@@@@
//		this.actionsExecutorService.submit(JOB);

		// 파일에 대한 변경을 감시 할 경로를 등록한다.
		this.downloadFileWriteLocation = new File(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION));

		// @@@@@ 설정값 변경
		// 파일에 대한 변경을 감시하는 서비스를 시작한다.
		this.watchExecutorTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// 파일 목록 로드
				String fileList[] = TorrentSupervisoryControlService.this.downloadFileWriteLocation.list(new FilenameFilter() {

				    @Override
				    public boolean accept(File dir, String name) { 
				        return true;//name.startsWith("TEMP");        // TEMP로 시작하는 파일들만 return 
				    }
				 
				});
				// 폴더는 제거해야 됨
				if(fileList.length > 0){
				    for(int i=0; i < fileList.length; i++){
				        System.out.println(fileList[i]);
				    }
				}

				// 파일 목록에 따라서 액션익스큐터서비스에 추가
//				// @@@@@
//				System.out.println("################## 1");
//				// @@@@@ 이전에 생성된 파일들은 수동으로 추가해줘야됨, 누락되지않도록 해야됨
//				// watch 서비스가 아니라 10초에 한번씩 파일목록을 뽑아서 수동으로 추가하는건??? 나쁘지 않을것 같음, 타이머 돌리면 됨
			}
		}, 500, 5 * 1000);// @@@@@

		return true;
	}

	@Override
	public void stop() {
		if (this.watchExecutorTimer != null) {
			this.watchExecutorTimer.cancel();
		}
		if (this.actionsExecutorService != null) {
			this.actionsExecutorService.shutdown();
		}

		this.watchExecutorTimer = null;
		this.actionsExecutorService = null;
	}

}
