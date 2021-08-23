package com.darkkaiser.torrentad.net.ftp;

import com.darkkaiser.torrentad.util.notifyapi.NotifyApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.jsoup.helper.StringUtil;

import java.io.*;

@Slf4j
public class FTPClient {

	private org.apache.commons.net.ftp.FTPClient ftpClient;

	public boolean connect(final String host, final int port, final String user, final String password) throws Exception {
		if (StringUtil.isBlank(host) == true)
			throw new IllegalArgumentException("host는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(user) == true)
			throw new IllegalArgumentException("user는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(password) == true)
			throw new IllegalArgumentException("password는 빈 문자열을 허용하지 않습니다.");

		disconnect();

		this.ftpClient = new org.apache.commons.net.ftp.FTPClient();

		FTPClientConfig ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		this.ftpClient.configure(ftpClientConfig);

		try {
			// 파일명의 한글 깨짐을 방지하기 위해 연결을 맺기 전에 설정한다. 
			this.ftpClient.setControlEncoding("utf-8");

			this.ftpClient.connect(host, port);

			this.ftpClient.enterLocalPassiveMode();

			// 응답이 정상적인지 확인한다.
			int nReply = this.ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(nReply) == false) {
				disconnect();

				log.error("FTP server refused connection.");
				NotifyApiClient.sendNotifyMessage("FTP server refused connection.", true);

				return false;
			}

			this.ftpClient.login(user, password);

			this.ftpClient.setSoTimeout(10000);
			this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (final Exception e) {
			log.error(null, e);
			NotifyApiClient.sendNotifyMessage(e.toString(), true);
			return false;
		}

		return true;
	}

	public void disconnect() throws Exception {
		if (this.ftpClient == null)
			return;

		if (this.ftpClient.isConnected() == true) {
			try {
				this.ftpClient.logout();
			} catch (final IOException e) {
				log.error("error logging off the ftp client: {}", e.getMessage());
			}

			try {
				this.ftpClient.disconnect();
			} catch (final IOException e) {
				log.error("error disconnecting from the ftp client: {}", e.getMessage());
			}
		}

		this.ftpClient = null;
	}

	public boolean isConnected() {
		return this.ftpClient != null && this.ftpClient.isConnected();
	}

	public boolean download(final File file, final String remotePath) throws Exception {
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
			if (this.ftpClient.retrieveFile(remotePath, bos) == false) {
				final String message = String.format("FTP 서버에서의 파일 다운로드가 실패하였습니다.(%s)", file.getAbsolutePath());

				log.error(message);
				NotifyApiClient.sendNotifyMessage(message, true);

				return false;
			}

			return true;
		}
	}

	public boolean upload(final File file, final String remotePath) throws Exception {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			if (this.ftpClient.storeFile(remotePath, bis) == false) {
				final String message = String.format("FTP 서버로의 파일 업로드가 실패하였습니다.(%s)", file.getAbsolutePath());

				log.error(message);
				NotifyApiClient.sendNotifyMessage(message, true);

				return false;
			}

			return true;
		}
	}

}
