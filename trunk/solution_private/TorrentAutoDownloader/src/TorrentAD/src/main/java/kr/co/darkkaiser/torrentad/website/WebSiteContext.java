package kr.co.darkkaiser.torrentad.website;

public interface WebSiteContext {

	String getName();
	
	WebSiteAccount getAccount();

	void setAccount(WebSiteAccount account);
	
	void validate();

	boolean isValid();

}
