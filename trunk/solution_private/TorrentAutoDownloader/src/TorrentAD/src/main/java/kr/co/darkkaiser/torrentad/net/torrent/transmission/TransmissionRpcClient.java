package kr.co.darkkaiser.torrentad.net.torrent.transmission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.MethodResult;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.SessionGetMethodResult;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentAddMethodResult;

public class TransmissionRpcClient implements TorrentClient {

	private static final Logger logger = LoggerFactory.getLogger(TransmissionRpcClient.class);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";
	
	private final String rpcURL;
	
	private String sessionId;
	
	private String authorization;

	private final Gson gson = new GsonBuilder().create();
	
	public TransmissionRpcClient(final String rpcURL) {
		if (StringUtil.isBlank(rpcURL) == true)
			throw new IllegalArgumentException("rpcURL은 빈 문자열을 허용하지 않습니다.");

		this.rpcURL = rpcURL;
	}

	@Override
	public boolean connect(final String user, final String password) throws Exception {
		if (StringUtil.isBlank(user) == true)
			throw new IllegalArgumentException("user는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(password) == true)
			throw new IllegalArgumentException("password는 빈 문자열을 허용하지 않습니다.");

		disconnect();

		// Authorization 헤더 문자열을 생성한다.
		String authorization = String.format("Basic %s", new String(Base64.encodeBase64(String.format("%s:%s", user, password).getBytes())));

		Connection.Response response = Jsoup.connect(this.rpcURL)
				.userAgent(USER_AGENT)
				.header("Authorization", authorization)
				.requestBody("{\"method\": \"session-get\"}")
				.method(Connection.Method.POST)
				.ignoreHttpErrors(true)
				.ignoreContentType(true)
				.execute();

		int statusCode = response.statusCode();
		if (statusCode == HttpStatus.SC_CONFLICT) {
			String sessionId = response.header("X-Transmission-Session-Id");

			response = Jsoup.connect(this.rpcURL)
					.userAgent(USER_AGENT)
					.header("Authorization", authorization)
					.header("X-Transmission-Session-Id", sessionId)
					.requestBody("{\"method\": \"session-get\"}")
					.method(Connection.Method.POST)
					.ignoreHttpErrors(true)
					.ignoreContentType(true)
					.execute();

			if (response.statusCode() != HttpStatus.SC_OK) {
				logger.error("POST " + this.rpcURL + "(X-Transmission-Session-Id:" + sessionId + ")" + " returned " + response.statusCode() + ": " + response.statusMessage());
				return false;
			}

			String result = response.parse().body().html();
			MethodResult methodResult = gson.fromJson(result, SessionGetMethodResult.class);
			if (methodResult.isResultSuccess() == false) {
				logger.error("토렌트 서버에서 수신된 데이터가 success가 아닙니다.(method:session-get, 수신된 데이터:{})", result);
				return false;
			}

			this.sessionId = sessionId;
			this.authorization = authorization;
		} else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
			logger.error("POST " + this.rpcURL + " returned " + response.statusCode() + ": " + response.statusMessage() + ": 사용자 인증이 실패하였습니다.");
			return false;
		} else {
			logger.error("POST " + this.rpcURL + " returned " + response.statusCode() + ": " + response.statusMessage());
			return false;
		}

		return true;
	}
	
	@Override
	public void disconnect() throws Exception {
		this.sessionId = null;
		this.authorization = null;
	}

	@Override
	public boolean isConnected() {
		if (StringUtil.isBlank(this.sessionId) == true || StringUtil.isBlank(this.authorization) == true)
			return false;

		return true;
	}

	@Override
	public boolean addTorrent(File file, boolean paused) throws Exception {
		if (file == null)
			throw new NullPointerException("file");
		
		// @@@@@
		if (isConnected() == false)
			return false;

		String filePath = file.getAbsolutePath().toLowerCase();
		if (filePath.endsWith(".torrent") == false) {
			throw new IllegalArgumentException("토렌트 파일이 아닙니다.");
		}

		Connection.Response response = Jsoup.connect(this.rpcURL)
				.userAgent(USER_AGENT)
				.header("Authorization", this.authorization)
				.header("X-Transmission-Session-Id", this.sessionId)
				.requestBody(String.format("{ \"method\":\"torrent-add\", \"arguments\":{\"metainfo\":\"%s\", \"paused\":\"%s\"}}", encodeFileToBase64(file), Boolean.toString(paused)))
				.method(Connection.Method.POST)
				.ignoreHttpErrors(true)
				.ignoreContentType(true)
				.execute();

		if (response.statusCode() != HttpStatus.SC_OK) {
			logger.error("POST " + this.rpcURL + "(X-Transmission-Session-Id:" + sessionId + ")" + " returned " + response.statusCode() + ": " + response.statusMessage());
			return false;
		}

		String result = response.parse().body().html();
		MethodResult methodResult = gson.fromJson(result, TorrentAddMethodResult.class);
		if (methodResult.isResultSuccess() == false) {
			logger.error("토렌트 서버에서 수신된 데이터가 success가 아닙니다.(method:torrent-add, 수신된 데이터:{})", result);
			return false;
		}
		
		// @@@@@ 실패처리

		return true;
	}

	private String encodeFileToBase64(File file) throws IOException {
		assert file != null;

		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bytes = new byte[(int) file.length()];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}

			return new String(Base64.encodeBase64(bytes));
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}

}
