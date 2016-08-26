package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoWebSiteSearchContext;

public enum WebSite {

	BOGOBOGO("보고보고") {
		@Override
		public WebSiteHandler createHandler() {
			return new BogoBogoWebSite();
		}

		@Override
		public WebSiteAccount createAccount(String id, String password) {
			return new WebSiteAccountAdapter(id, password);
		}

		@Override
		public WebSiteSearchContext createSearchContext() {
			return new BogoBogoWebSiteSearchContext();
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
			throw new IllegalArgumentException("name은 빈 문자열을 허용하지 않습니다.");
		}

		if (name.equals(BOGOBOGO.toString()) == true) {
			return BOGOBOGO;
		}

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSite.class.getSimpleName(), name));
	}

	@Override
	public String toString() {
		return getName();
	}

	public abstract WebSiteHandler createHandler();
	public abstract WebSiteAccount createAccount(String id, String password);
	public abstract WebSiteSearchContext createSearchContext();

}
