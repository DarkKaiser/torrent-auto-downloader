package com.darkkaiser.torrentad.service.au.transmitter;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.net.ftp.FTPClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

@Slf4j
public class FTPFileTransmitter extends AbstractFileTransmitter {

	private FTPClient ftpClient;

	public FTPFileTransmitter(final Configuration configuration) {
		super(configuration);
	}

	@Override
	public void prepare() throws Exception {
		if (this.ftpClient != null && this.ftpClient.isConnected() == true) 
			return;

		String host = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_HOST);
		String port = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_PORT);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_ACCOUNT_ID);
		String password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_ACCOUNT_PASSWORD));

		this.ftpClient = new FTPClient();
		if (this.ftpClient.connect(host, Integer.parseInt(port), id, password) == false)
			log.warn(String.format("FTP 서버 접속이 실패하였습니다.(Host:%s, Port:%s, Id:%s)", host, port, id));
	}

	@Override
	public boolean transmit(final File file) throws Exception {
		Objects.requireNonNull(file, "file");
		Objects.requireNonNull(this.ftpClient, "ftpClient");

		if (this.ftpClient.isConnected() == false)
			throw new IllegalStateException("FTP 서버에 연결되어 있지 않습니다.");

		assert file.isDirectory() == false;

		if (file.exists() == false) {
			throw new FileNotFoundException(file.getAbsolutePath());
		} else {
			String remotePath = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_UPLOAD_LOCATION);
			if (remotePath.endsWith("/") == true) {
				remotePath += file.getName();
			} else {
				remotePath += "/" + file.getName();
			}

			return this.ftpClient.upload(file, remotePath);
		}
	}

	@Override
	public boolean transmitFinished() {
		if (this.ftpClient != null) {
			try {
				this.ftpClient.disconnect();
			} catch (final Exception e) {
				log.error(null, e);
			}

			this.ftpClient = null;
		}

		return true;
	}

	@Override
	public boolean support(File file) {
		Objects.requireNonNull(file, "file");

		return file.isDirectory() != true;
	}

}
