package kr.co.darkkaiser.torrentad.website;

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
	
	public static WebSite get(String name) {
		if (BOGOBOGO.equals(name) == true) {
			return BOGOBOGO;
		}
		
		// @@@@@
		return BOGOBOGO;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public abstract WebSiteHandler createHandler();
	public abstract WebSiteAccount createAccount(String id, String password);

}
