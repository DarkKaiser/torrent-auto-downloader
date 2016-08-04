package kr.co.darkkaiser.torrentad.website;

public interface WebSiteContext<B extends WebSiteContext<B>> {

	String getDomain();
	
	B setDomain(String domain);

	Account getAccount();

	B setAccount(Account account);

}
