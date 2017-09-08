package com.darkkaiser.torrentad.service.ad.task;

public class UnsupportedTaskException extends RuntimeException {

	private static final long serialVersionUID = 7317989810880510772L;
	
	public UnsupportedTaskException() {
		super();
	}

	public UnsupportedTaskException(final String s) {
		super(s);
	}

	public UnsupportedTaskException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedTaskException(final Throwable cause) {
		super(cause);
	}

}
