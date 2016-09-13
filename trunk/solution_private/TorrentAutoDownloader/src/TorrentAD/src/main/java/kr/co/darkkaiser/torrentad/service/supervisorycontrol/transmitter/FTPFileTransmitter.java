package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

// @@@@@
public class FTPFileTransmitter implements FileTransmitter {

	public FTPFileTransmitter() {
	}

	@Override
	public void prepare() {
		// @@@@@
	}
	
	@Override
	public boolean transmit() {
		// @@@@@ prepare()
		return true;
	}

	@Override
	public void transmitCompleted() {
		// @@@@@
	}

	@Override
	public boolean support(File file) {
		return true;
	}

}
