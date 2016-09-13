package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

import kr.co.darkkaiser.torrentad.net.ftp.FTPClient;

public class FTPFileTransmitter implements FileTransmitter {

	private FTPClient ftpClient;
	
	public FTPFileTransmitter() {
		this.ftpClient = new FTPClient();
	}

	@Override
	public void prepare() {
		// @@@@@
		// FTP 서버에 접속한다.
	}

	@Override
	public boolean transmit(File file) {
		if (file == null)
			throw new NullPointerException("file");

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

		if (file.isDirectory() == true)
			return false;
		
		return true;
	}

}
