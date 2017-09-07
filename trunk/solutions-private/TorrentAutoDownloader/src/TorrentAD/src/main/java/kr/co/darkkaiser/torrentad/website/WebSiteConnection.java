package kr.co.darkkaiser.torrentad.website;

public interface WebSiteConnection {

	void login(WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	boolean isLogin();

}
