package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

public interface WebSiteHandler {

	void login(WebSiteAccount account) throws IOException;

	void logout();

}
