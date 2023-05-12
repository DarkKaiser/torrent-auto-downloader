package com.darkkaiser.torrentad.website;

public class IncorrectLoginAccountException extends RuntimeException {

	private static final long serialVersionUID = -2407249496917325361L;

	public IncorrectLoginAccountException(final String s) {
		super(s);
	}
    
}
