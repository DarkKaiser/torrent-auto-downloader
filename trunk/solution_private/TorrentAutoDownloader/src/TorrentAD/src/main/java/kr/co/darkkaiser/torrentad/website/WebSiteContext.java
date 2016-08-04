package kr.co.darkkaiser.torrentad.website;

import kr.co.darkkaiser.torrentad.website.account.Account;

public interface WebSiteContext<B extends WebSiteContext<B>> {
	
	String protocol();
	
	B protocol(String protocol);

	String domain();
	
	B domain(String domain);

	Account account();

	B account(String id, String password);

	boolean valid();
	
}
