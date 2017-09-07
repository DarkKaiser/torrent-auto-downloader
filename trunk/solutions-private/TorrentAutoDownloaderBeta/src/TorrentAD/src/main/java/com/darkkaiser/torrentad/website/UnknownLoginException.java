package com.darkkaiser.torrentad.website;

public class UnknownLoginException extends RuntimeException {

	private static final long serialVersionUID = 7704336692766699056L;

	public UnknownLoginException() {
		super();
	}

	public UnknownLoginException(String s) {
		super(s);
	}

	public UnknownLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownLoginException(Throwable cause) {
		super(cause);
	}

}
