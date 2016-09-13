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

// @@@@@
public class FTPClient {

	private org.apache.commons.net.ftp.FTPClient ftpClient;

	public FTPClient() {
	}

	public boolean connect(final String host, final int port, final String user, final String password) throws Exception {
		this.ftpClient = new org.apache.commons.net.ftp.FTPClient();

		FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		ftpClient.configure(config);

		try {
			ftpClient.connect(host, port);
			ftpClient.enterLocalPassiveMode();

			int nReply = ftpClient.getReplyCode(); // ③ 응답이 정상적인지 확인하기위해 응답을 받아옴
			if (!FTPReply.isPositiveCompletion(nReply)) { // ④ 응답이 정삭적인지 확인
				ftpClient.disconnect(); // ⑤ 응답이 비정상이면 연결해제
				ftpClient = null; // ftpClient = null;
				System.out.println("FTP server refused connection.");
				return false; // return false;
			}
			
			System.out.print(ftpClient.getReplyString()); // 응답 메세지를 찍어봅시다
			
			ftpClient.setSoTimeout(10000); // 현재 커넥션 timeout을 millisecond
			ftpClient.login(user, password);

			ftpClient.setControlEncoding("utf-8");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // ⑧ 파일 및 전송형태를 바이너리로
															// 설정
		} catch (Exception e) {
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
				System.err.println("error logging off the ftp client: " + e.getMessage());
			}
			
			try {
				this.ftpClient.disconnect();
			} catch (IOException e) {
				System.err.println("error disconnecting from the ftp client: " + e.getMessage());
			}
		}

		this.ftpClient = null;
	}

	public boolean isConnected() {
		if (this.ftpClient == null)
			return false;
		
		return this.ftpClient.isConnected();
	}

	public void download(final String localPath, final String remotePath) throws Exception {
		BufferedOutputStream bos = null;
		
		try {
			bos = new BufferedOutputStream(new FileOutputStream(localPath));
			this.ftpClient.retrieveFile(remotePath, bos);
		} finally {
			if (bos != null)
				bos.close();
		}
	}

	public void upload(final String localPath, final String remotePath) throws Exception {
		boolean done = false;
		BufferedInputStream bis = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(new File(localPath)));
			done = this.ftpClient.storeFile(remotePath, bis);
		} finally {
			if (bis != null)
				bis.close();
		}

		if (done)
			System.out.println("The first file is uploaded successfully.");
	}

	// OutputStream으로 업로드 예제
	// // APPROACH #2: uploads second file using an OutputStream
	// File secondLocalFile = new File("E:/Test/Report.doc");
	// String secondRemoteFile = "test/Report.doc";
	// inputStream = new FileInputStream(secondLocalFile);
	//
	// System.out.println("Start uploading second file");
	// OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
	// byte[] bytesIn = new byte[4096];
	// int read = 0;
	//
	// while ((read = inputStream.read(bytesIn)) != -1) {
	// outputStream.write(bytesIn, 0, read);
	// }
	// inputStream.close();
	// outputStream.close();
	//
	// boolean completed = ftpClient.completePendingCommand();
	// if (completed) {
	// System.out.println("The second file is uploaded successfully.");
	// }

}
