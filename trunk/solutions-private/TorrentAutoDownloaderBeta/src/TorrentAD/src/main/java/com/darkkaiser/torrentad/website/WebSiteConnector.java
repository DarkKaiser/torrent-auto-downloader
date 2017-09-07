package com.darkkaiser.torrentad.website;

public interface WebSiteConnector {

	boolean login();

	boolean logout();

	boolean isLogin();

	String getOwner();

	WebSite getSite();

	WebSiteConnection getConnection();

}
