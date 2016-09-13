package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;
import java.io.UnsupportedEncodingException;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.ftp.FTPClient;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class FTPFileTransmitter implements FileTransmitter {

	private FTPClient ftpClient;

	private final Configuration configuration;

	private final AES256Util aes256 = new AES256Util();

	public FTPFileTransmitter(Configuration configuration) throws UnsupportedEncodingException {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
	}

	@Override
	public void prepare() {
		// @@@@@
		// FTP 서버에 접속한다.

		this.ftpClient = new FTPClient();
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
