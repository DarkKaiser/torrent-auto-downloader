package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.ftp.FTPClient;

public class FTPFileTransmitter extends AbstractFileTransmitter {

	private static final Logger logger = LoggerFactory.getLogger(FTPFileTransmitter.class);
	
	private FTPClient ftpClient;

	public FTPFileTransmitter(Configuration configuration) throws UnsupportedEncodingException {
		super(configuration);
	}

	@Override
	public void prepare() throws Exception {
		// @@@@@
		String host = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_HOST);
		String port = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_PORT);

		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_ACCOUNT_ID);
		String password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_ACCOUNT_PASSWORD));

		// FTP 서버에 접속한다.
		this.ftpClient = new FTPClient();
	}

	@Override
	public boolean transmit(File file) throws Exception {
		if (file == null)
			throw new NullPointerException("file");

		// @@@@@

		return true;
	}

	@Override
	public boolean transmitFinished() throws Exception {
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
