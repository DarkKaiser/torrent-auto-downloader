package com.darkkaiser.torrentad.service.ad.task;

public class UnsupportedTaskException extends RuntimeException {

	private static final long serialVersionUID = 7317989810880510772L;
	
	public UnsupportedTaskException() {
		super();
	}

	public UnsupportedTaskException(String s) {
		super(s);
	}

	public UnsupportedTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedTaskException(Throwable cause) {
		super(cause);
	}

}
