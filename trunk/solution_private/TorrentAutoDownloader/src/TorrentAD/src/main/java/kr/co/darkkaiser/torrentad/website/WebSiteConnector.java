package kr.co.darkkaiser.torrentad.website;

public interface WebSiteConnector {

	boolean login();

	boolean logout();

	WebSite getSite();

	WebSiteConnection getConnection();

}
