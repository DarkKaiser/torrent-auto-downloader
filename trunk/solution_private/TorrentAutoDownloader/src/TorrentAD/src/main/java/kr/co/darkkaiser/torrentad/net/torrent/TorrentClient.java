package kr.co.darkkaiser.torrentad.net.torrent;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TorrentClient {

	private static final Logger logger = LoggerFactory.getLogger(TorrentClient.class);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	private Connection.Response loginConnResponse;
	
	private String header;

	// @@@@@
	public boolean connect(final String host, final String user, final String password) throws Exception {
		// @@@@@
		Connection.Response response = Jsoup.connect("http://darkkaiser.gonetis.com:9091/transmission/rpc")
				.userAgent(USER_AGENT)
				.header("Authorization", "Basic ZGFya2thaXNlcjpEcmVhbVdha3VXYWt1Nzg=")
				.method(Connection.Method.GET)
				.ignoreHttpErrors(true)
				.execute();

		int statusCode = response.statusCode();
		System.out.println(statusCode);
		this.header = response.header("X-Transmission-Session-Id");
		
		return false;
	}
	
	public void disconnect() throws Exception {
		// @@@@@
		this.loginConnResponse = null;
	}
	
	public boolean isConnected() {
		if (this.loginConnResponse == null)
			return false;

		return true;
		
//		if (this.ftpClient == null)
//			return false;
//
//		return this.ftpClient.isConnected();
	}

}
