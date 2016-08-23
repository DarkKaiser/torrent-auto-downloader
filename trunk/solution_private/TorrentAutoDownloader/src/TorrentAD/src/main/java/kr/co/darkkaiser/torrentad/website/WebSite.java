package kr.co.darkkaiser.torrentad.website;

public enum WebSite {

	BOGOBOGO("보고보고") {
		@Override
		public WebSiteHandler createWebSite() {
			// @@@@@
			return new BogoBogoWebSite();
		}

		@Override
		public WebSiteAccount createWebSiteAccount(String id, String password) {
			// @@@@@
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
	
	@Override
	public String toString() {
		return this.name;
	}

	abstract WebSiteHandler createWebSite();
	abstract WebSiteAccount createWebSiteAccount(String id, String password);
	
}
