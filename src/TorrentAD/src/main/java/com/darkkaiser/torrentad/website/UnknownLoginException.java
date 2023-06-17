package com.darkkaiser.torrentad.website;

import java.io.Serial;

public class UnknownLoginException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 7704336692766699056L;

	public UnknownLoginException(final String s) {
		super(s);
	}

}
