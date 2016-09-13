package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.ftp.FTPClient;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class FTPFileTransmitter implements FileTransmitter {

	private FTPClient ftpClient;
	
	public FTPFileTransmitter(AES256Util aes256, Configuration configuration) {
		if (aes256 == null)
			throw new NullPointerException("aes256");
		if (configuration == null)
			throw new NullPointerException("configuration");

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
