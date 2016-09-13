package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

public interface FileTransmitter {

	// @@@@@
	void prepare();
	void cleanup();
	boolean transmit();

	boolean support(File file);

}
