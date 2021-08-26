package com.darkkaiser.torrentad.website;

public interface WebSiteAccount {

	String id();

	String password();

	void validate();

	boolean isValid();

}
