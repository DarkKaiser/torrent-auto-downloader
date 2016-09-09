package kr.co.darkkaiser.torrentad.service.observation;

import kr.co.darkkaiser.torrentad.service.Service;

public class TorrentObservationService implements Service {

	// @@@@@
	// 특정폴더 파일 모니터링(계속)
	// ftp 접속 후 업로드(파일이 있을때 이루어짐)
	// 토렌트 파일 업로드(rpc 이용) 파일이 있을때, 토렌트가 유휴할때 이루어짐
	// 토렌트 완료된거 삭제(수시로 이루어짐)
	
	// 파일을 모니터링중에 파일이 생성되면 이때 토렌트로 업로드
	

	@Override
	public boolean start() throws Exception {
		return true;
	}

	@Override
	public void stop() {
	}

}
