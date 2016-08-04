package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

import kr.co.darkkaiser.torrentad.website.account.Account;

public interface WebSiteHandler {

	void login(Account account) throws IOException;

	void logout();

}
