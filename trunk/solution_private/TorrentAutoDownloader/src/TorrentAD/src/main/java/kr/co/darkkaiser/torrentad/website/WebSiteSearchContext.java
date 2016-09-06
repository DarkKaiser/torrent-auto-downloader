package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchContext {

	WebSite getWebSite();

	void setBoardName(String name) throws Exception;

	void validate();

	boolean isValid();

}
