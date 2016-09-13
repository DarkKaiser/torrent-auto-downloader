package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

public class TorrentFileTransmitter implements FileTransmitter {
	
	public TorrentFileTransmitter() {
	}

	// @@@@@
	//@@@@@ 토렌트 파일은 정지상태로 올리기
	//transmission rpc
	//https://github.com/stil4m/transmission-rpc-java
	//https://sourceforge.net/projects/transmission-rj/

	@Override
	public void prepare() {
		// @@@@@
	}

	@Override
	public void cleanup() {
		// @@@@@
	}

	@Override
	public boolean transmit() {
		// @@@@@
		return true;
	}

	@Override
	public boolean support(File file) {
		// @@@@@
		return false;
	}

}
