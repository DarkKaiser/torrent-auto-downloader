package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

// @@@@@
public class FtpFileTransmitter implements FileTransmitter {

	public FtpFileTransmitter() {
	}

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
		// @@@@@ prepare()
		return true;
	}

	@Override
	public boolean support(File file) {
		return true;
	}

}
