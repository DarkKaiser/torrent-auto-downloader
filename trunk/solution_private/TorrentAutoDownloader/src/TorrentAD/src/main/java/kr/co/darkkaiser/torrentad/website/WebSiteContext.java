package kr.co.darkkaiser.torrentad.website;

public interface WebSiteContext<B extends WebSiteContext<B>> {

	WebSiteAccount getAccount();

	B setAccount(WebSiteAccount account);
	
	void validate();

	boolean isValid();

}
