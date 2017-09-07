package com.darkkaiser.torrentad.service.au.transmitter;

import java.io.File;

public interface FileTransmitter {

	void prepare() throws Exception;

	boolean transmit(File file) throws Exception;

	boolean transmitFinished();

	boolean support(File file);

}
