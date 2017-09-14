package com.darkkaiser.torrentad.service.au.action;

public class UnsupportedActionException extends RuntimeException {

	private static final long serialVersionUID = 2101408061616234747L;

	public UnsupportedActionException() {
		super();
	}

	public UnsupportedActionException(final String s) {
		super(s);
	}

	public UnsupportedActionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedActionException(final Throwable cause) {
		super(cause);
	}

}
