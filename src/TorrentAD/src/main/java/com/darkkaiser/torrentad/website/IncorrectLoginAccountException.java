package com.darkkaiser.torrentad.website;

import java.io.Serial;

public class IncorrectLoginAccountException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -2407249496917325361L;

	public IncorrectLoginAccountException(final String s) {
		super(s);
	}
    
}
