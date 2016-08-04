package kr.co.darkkaiser.torrentad.website.account;

public final class AccountFactory {

	private AccountFactory() {
	}
	
	public static Account newAccount(String id, String password) {
		return new DefaultAccount(id, password);
	}
	
}
