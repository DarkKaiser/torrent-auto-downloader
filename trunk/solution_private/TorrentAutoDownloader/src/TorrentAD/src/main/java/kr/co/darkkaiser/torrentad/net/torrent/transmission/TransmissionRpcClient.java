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
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;

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

		if (isConnected() == false) {
			logger.error("토렌트 서버와 접속중인 상태가 아닙니다.");
			return false;			
		}

		String filePath = file.getAbsolutePath().toLowerCase();
		if (filePath.endsWith(".torrent") == false) {
			logger.error("토렌트 서버에 추가하시려는 파일이 토렌트 파일이 아닙니다.({})", file.getAbsolutePath());
			return false;
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
		TorrentAddMethodResult methodResult = gson.fromJson(result, TorrentAddMethodResult.class);
		if (methodResult.isResultSuccess() == false) {
			logger.error("토렌트 서버에서 수신된 데이터가 success가 아닙니다.(method:torrent-add, 수신된 데이터:{})", result);
			return false;
		}

		if (methodResult.arguments == null || (methodResult.arguments.torrentAdded == null && methodResult.arguments.torrentDuplicate == null)) {
			logger.error("토렌트 서버에서 수신된 데이터의 파싱 결과가 유효하지 않습니다.(method:torrent-add, 수신된 데이터:{})", result);
			return false;
		} else if (methodResult.arguments.torrentDuplicate != null) {
			assert methodResult.arguments.torrentAdded == null;
			logger.warn("토렌트 서버에 이미 등록되어 있는 토렌트입니다.(method:torrent-add, 파일:{})", file.getAbsolutePath());
			return true;
		}

		assert methodResult.arguments.torrentAdded != null;

		return true;
	}
	
	@Override
	public void get() throws IOException {
		// @@@@@
		System.out.println("##################");
		Connection.Response response = Jsoup.connect(this.rpcURL)
				.userAgent(USER_AGENT)
				.header("Authorization", this.authorization)
				.header("X-Transmission-Session-Id", this.sessionId)
				.requestBody("{\"method\": \"torrent-get\", \"arguments\":{\"fields\":[\"id\",\"isFinished\",\"error\",\"errorString\",\"isStalled\",\"status\"]}}")
				.method(Connection.Method.POST)
				.ignoreHttpErrors(true)
				.ignoreContentType(true)
				.execute();

//		{"arguments":{"torrents":[
//		                          {"error":0,"errorString":"","id":3,"isFinished":false,"isStalled":false,"status":0},
//		                          {"error":0,"errorString":"","id":4,"isFinished":false,"isStalled":false,"status":0},
//		                          {"error":0,"errorString":"","id":5,"isFinished":false,"isStalled":false,"status":0},
//		                          {"error":0,"errorString":"","id":6,"isFinished":false,"isStalled":false,"status":0}]},"result":"success"}

		if (response.statusCode() != HttpStatus.SC_OK) {
			logger.error("POST " + this.rpcURL + "(X-Transmission-Session-Id:" + sessionId + ")" + " returned " + response.statusCode() + ": " + response.statusMessage());
			return;
		}

		String result = response.parse().body().html();
		TorrentGetMethodResult methodResult = gson.fromJson(result, TorrentGetMethodResult.class);

		System.out.println(result);
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
