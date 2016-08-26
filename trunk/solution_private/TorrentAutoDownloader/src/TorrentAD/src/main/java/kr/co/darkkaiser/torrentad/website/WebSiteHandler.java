package kr.co.darkkaiser.torrentad.website;

public interface WebSiteHandler {

	void login(WebSiteAccount account) throws Exception;

	void logout() throws Exception;

	boolean isLogin();

	//@@@@@
	void search(WebSiteSearchContext taskContext);

}
