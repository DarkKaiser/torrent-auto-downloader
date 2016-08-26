package kr.co.darkkaiser.torrentad.website.torrent.bogobogo;

import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteAccountAdapter;

public final class BogoBogoWebSiteAccount extends WebSiteAccountAdapter {
	
	public BogoBogoWebSiteAccount(String id, String password) {
		super(WebSite.BOGOBOGO, id, password);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public boolean isValid() {
		return super.isValid();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoWebSiteAccount.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
