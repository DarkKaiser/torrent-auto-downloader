package com.darkkaiser.torrentad.website;

public class EmptySearchKeywordException extends RuntimeException {

	private static final long serialVersionUID = -4657183351987411925L;

	public EmptySearchKeywordException() {
		super();
	}

	public EmptySearchKeywordException(String s) {
		super(s);
	}

	public EmptySearchKeywordException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptySearchKeywordException(Throwable cause) {
		super(cause);
	}
	
}
