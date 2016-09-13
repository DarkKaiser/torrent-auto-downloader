package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

public class FtpFileTransmitter implements FileTransmitter {

	// @@@@@

	@Override
	public boolean supportedFile(File file) {
		return true;
	}

}
