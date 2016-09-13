package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

public interface FileTransmitter {

	void prepare() throws Exception;

	boolean transmit(File file) throws Exception;

	boolean transmitFinished() throws Exception;

	boolean support(File file);

}
