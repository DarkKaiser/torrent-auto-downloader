package com.darkkaiser.torrentad.website;

public interface WebSiteContext {

	String getName();

	String getBaseURL();

	WebSiteAccount getAccount();

	void setAccount(final WebSiteAccount account);

}
