package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class TorrentFileTransmitter implements FileTransmitter {
	
	public TorrentFileTransmitter(AES256Util aes256, Configuration configuration) {
		if (aes256 == null)
			throw new NullPointerException("aes256");
		if (configuration == null)
			throw new NullPointerException("configuration");
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
	public boolean transmit(File file) {
		// @@@@@
		return true;
	}

	@Override
	public boolean transmitFinished() {
		// @@@@@
		return true;
	}

	@Override
	public boolean support(File file) {
		if (file == null)
			throw new NullPointerException("file");

		return file.getName().toLowerCase().endsWith(".torrent");
	}

}
