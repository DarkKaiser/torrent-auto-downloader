package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

public class TorrentFileTransmitter implements FileTransmitter {

	// @@@@@
	//@@@@@ 토렌트 파일은 정지상태로 올리기
	//transmission rpc
	//https://github.com/stil4m/transmission-rpc-java
	//https://sourceforge.net/projects/transmission-rj/

	@Override
	public boolean supportedFile(File file) {
		// @@@@@
		return false;
	}

}
