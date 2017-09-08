package com.darkkaiser.torrentad.website;

public interface WebSiteConnection {

	void login(final WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	boolean isLogin();

}
