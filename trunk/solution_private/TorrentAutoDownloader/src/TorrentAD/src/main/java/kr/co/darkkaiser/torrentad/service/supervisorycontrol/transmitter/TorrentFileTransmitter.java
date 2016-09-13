package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;
import java.io.UnsupportedEncodingException;

import kr.co.darkkaiser.torrentad.config.Configuration;

public class TorrentFileTransmitter extends AbstractFileTransmitter {
	
	public TorrentFileTransmitter(Configuration configuration) throws UnsupportedEncodingException {
		super(configuration);
	}

	// @@@@@
	//@@@@@ 토렌트 파일은 정지상태로 올리기
	//transmission rpc
	//https://github.com/stil4m/transmission-rpc-java
	//https://sourceforge.net/projects/transmission-rj/

	@Override
	public void prepare() throws Exception {
		// @@@@@
	}

	@Override
	public boolean transmit(File file) throws Exception {
		// @@@@@
		return true;
	}

	@Override
	public boolean transmitFinished() throws Exception {
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
