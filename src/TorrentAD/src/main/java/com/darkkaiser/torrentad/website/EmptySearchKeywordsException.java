package com.darkkaiser.torrentad.website;

import java.io.Serial;

public class EmptySearchKeywordsException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -4502267990254247102L;

	public EmptySearchKeywordsException(final String s) {
		super(s);
	}

}
