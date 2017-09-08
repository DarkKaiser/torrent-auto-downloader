package com.darkkaiser.torrentad.website;

public class EmptySearchKeywordsException extends RuntimeException {

	private static final long serialVersionUID = -4502267990254247102L;

	public EmptySearchKeywordsException() {
		super();
	}

	public EmptySearchKeywordsException(final String s) {
		super(s);
	}

	public EmptySearchKeywordsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EmptySearchKeywordsException(final Throwable cause) {
		super(cause);
	}
	
}
