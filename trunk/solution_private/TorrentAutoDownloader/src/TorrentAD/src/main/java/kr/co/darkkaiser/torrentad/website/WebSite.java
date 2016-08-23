package kr.co.darkkaiser.torrentad.website;

public enum WebSite {

	BOGOBOGO("보고보고");

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

}
