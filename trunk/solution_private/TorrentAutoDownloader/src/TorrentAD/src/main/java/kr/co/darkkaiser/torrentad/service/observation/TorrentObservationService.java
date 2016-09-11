package kr.co.darkkaiser.torrentad.service.observation;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.service.Service;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class TorrentObservationService implements Service {

	private ExecutorService tasksExecutorService;
	
	private final ConfigurationManager configurationManager;

	private final AES256Util aes256;

	public TorrentObservationService(AES256Util aes256, ConfigurationManager configurationManager) throws UnsupportedEncodingException {
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
		if (this.tasksExecutorService != null) {
			throw new IllegalStateException("tasksExecutorService 객체는 이미 초기화되었습니다");
		}
		if (this.configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		
		this.tasksExecutorService = Executors.newFixedThreadPool(1);

		// @@@@@
		// 특정폴더 파일 모니터링(계속) - JAVA NIO WatcherService 이용
		// ftp 접속 후 업로드(파일이 있을때 이루어짐)
		// 토렌트 파일 업로드(rpc 이용) 파일이 있을때, 토렌트가 유휴할때 이루어짐
		// 토렌트 완료된거 삭제(수시로 이루어짐)
		// 파일을 모니터링중에 파일이 생성되면 이때 토렌트로 업로드
//		this.tasksExecutorService.submit(JOB);

		return true;
	}

	@Override
	public void stop() {
		if (this.tasksExecutorService != null) {
			this.tasksExecutorService.shutdown();
		}

		this.tasksExecutorService = null;
	}

}
