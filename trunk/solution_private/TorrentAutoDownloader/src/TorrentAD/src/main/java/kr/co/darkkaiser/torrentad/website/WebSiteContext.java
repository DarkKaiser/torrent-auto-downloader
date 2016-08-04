package kr.co.darkkaiser.torrentad.website;

import kr.co.darkkaiser.torrentad.website.account.Account;

public interface WebSiteContext<B extends WebSiteContext<B>> {

	Account getAccount();

	B setAccount(Account account);

	boolean valid();
	
}
