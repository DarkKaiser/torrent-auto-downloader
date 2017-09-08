package kr.co.darkkaiser.torrentad.net.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPClient {

	private static final Logger logger = LoggerFactory.getLogger(FTPClient.class);

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
				logger.error("FTP server refused connection.");
				return false;
			}

			this.ftpClient.login(user, password);

			this.ftpClient.setSoTimeout(10000);
			this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (Exception e) {
			logger.error(null, e);
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
			} catch (IOException e) {
				logger.error("error logging off the ftp client: {}", e.getMessage());
			}

			try {
				this.ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("error disconnecting from the ftp client: {}", e.getMessage());
			}
		}

		this.ftpClient = null;
	}

	public boolean isConnected() {
		if (this.ftpClient == null)
			return false;

		return this.ftpClient.isConnected();
	}

	public boolean download(final File file, final String remotePath) throws Exception {
		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			if (this.ftpClient.retrieveFile(remotePath, bos) == false) {
				logger.error("FTP 서버에서의 파일 다운로드가 실패하였습니다.({})", file.getAbsolutePath());
				return false;
			}
			
			return true;
		} finally {
			if (bos != null)
				bos.close();
		}
	}

	public boolean upload(final File file, final String remotePath) throws Exception {
		BufferedInputStream bis = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			if (this.ftpClient.storeFile(remotePath, bis) == false) {
				logger.error("FTP 서버로의 파일 업로드가 실패하였습니다.({})", file.getAbsolutePath());
				return false;
			}

			return true;
		} finally {
			if (bis != null)
				bis.close();
		}
	}

}