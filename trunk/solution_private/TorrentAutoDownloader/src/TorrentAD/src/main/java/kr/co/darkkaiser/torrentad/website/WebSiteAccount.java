package kr.co.darkkaiser.torrentad.website;

public interface WebSiteAccount {

	String id();

	String password();

	void validate();

	boolean isValid();

}
