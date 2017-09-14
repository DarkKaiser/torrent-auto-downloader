package com.darkkaiser.torrentad.website;

public class UnknownLoginException extends RuntimeException {

	private static final long serialVersionUID = 7704336692766699056L;

	public UnknownLoginException() {
		super();
	}

	public UnknownLoginException(final String s) {
		super(s);
	}

	public UnknownLoginException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnknownLoginException(final Throwable cause) {
		super(cause);
	}

}
