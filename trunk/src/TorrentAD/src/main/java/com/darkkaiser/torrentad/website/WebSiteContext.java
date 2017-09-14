package com.darkkaiser.torrentad.website;

public interface WebSiteContext {

	String getName();

	WebSiteAccount getAccount();

	void setAccount(final WebSiteAccount account);

}
