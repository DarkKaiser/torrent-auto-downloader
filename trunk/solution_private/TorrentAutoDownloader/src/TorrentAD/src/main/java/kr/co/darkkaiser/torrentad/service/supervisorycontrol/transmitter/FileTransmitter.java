package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

public interface FileTransmitter {

	void prepare();
	
	boolean transmit();

	void transmitCompleted();

	boolean support(File file);

}
