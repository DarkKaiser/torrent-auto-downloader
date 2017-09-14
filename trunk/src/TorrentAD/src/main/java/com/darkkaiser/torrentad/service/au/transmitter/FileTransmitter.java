package com.darkkaiser.torrentad.service.au.transmitter;

import java.io.File;

public interface FileTransmitter {

	void prepare() throws Exception;

	boolean transmit(final File file) throws Exception;

	boolean transmitFinished();

	boolean support(final File file);

}
