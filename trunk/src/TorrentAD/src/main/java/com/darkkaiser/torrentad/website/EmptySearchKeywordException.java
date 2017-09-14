package com.darkkaiser.torrentad.website;

public class EmptySearchKeywordException extends RuntimeException {

	private static final long serialVersionUID = -4657183351987411925L;

	public EmptySearchKeywordException() {
		super();
	}

	public EmptySearchKeywordException(final String s) {
		super(s);
	}

	public EmptySearchKeywordException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EmptySearchKeywordException(final Throwable cause) {
		super(cause);
	}
	
}
