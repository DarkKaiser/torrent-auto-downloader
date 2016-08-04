package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BogoNetWebSite extends AbstractWebSite<BogoNetWebSite> {

	private static final Logger logger = LoggerFactory.getLogger(BogoNetWebSite.class);
	
	protected Connection.Response conn;
	
	public BogoNetWebSite() {
	}

	@Override
	public void login() throws IOException {
		// @@@@@
		Connection.Response loginForm = Jsoup.connect("https://zipbogo.net/cdsb/login_process.php")
				.userAgent("Mozilla")
				.data("mode", "login")
				.data("kinds", "outlogin")
				.data("user_id", this.account().id())
				.data("passwd", this.account().password())
                .method(Connection.Method.POST)
                .execute();
		Document loginFormDoc = loginForm.parse();
		
		// throw
//		LOGGER.error("異쒕젰�뙆�씪 �깮�꽦�떎�뙣(VM�뙆�씪={}, �깮�꽦�뙆�씪={})", vmFileClassPath, outputFileAbsolutePath);

		System.out.println(loginFormDoc);
	}

	@Override
	public void logout() {
		// @@@@@
	}

	public void search() {
		// @@@@@
	}

	public void download() {
		// @@@@@
	}

	public void upload() {
		// @@@@@
	}

}
