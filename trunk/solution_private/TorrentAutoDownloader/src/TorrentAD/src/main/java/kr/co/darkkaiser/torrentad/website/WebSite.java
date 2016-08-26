package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.torrent.bogobogo.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.torrent.bogobogo.BogoBogoWebSiteAccount;

public enum WebSite {

	BOGOBOGO("보고보고") {
		@Override
		public WebSiteHandler createHandler() {
			return new BogoBogoWebSite();
		}

		@Override
		public WebSiteAccount createAccount(String id, String password) {
			return new BogoBogoWebSiteAccount(id, password);
		}
	};

	private String name;
	
	private WebSite(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static WebSite fromString(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (StringUtil.isBlank(name) == true) {
			throw new IllegalArgumentException("name must not be empty.");
		}

		if (name.equals(BOGOBOGO.toString()) == true) {
			return BOGOBOGO;
		}

		throw new IllegalArgumentException(String.format("There is no value with name '%s' in Enum %s", name, WebSite.class.getSimpleName()));
	}

	@Override
	public String toString() {
		return this.name;
	}

	public abstract WebSiteHandler createHandler();
	public abstract WebSiteAccount createAccount(String id, String password);

}
