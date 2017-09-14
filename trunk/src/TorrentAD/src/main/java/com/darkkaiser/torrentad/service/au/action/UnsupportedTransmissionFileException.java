package com.darkkaiser.torrentad.service.au.action;

public class UnsupportedTransmissionFileException extends RuntimeException {

	private static final long serialVersionUID = 5699512946466171586L;

	public UnsupportedTransmissionFileException() {
		super();
	}

	public UnsupportedTransmissionFileException(final String s) {
		super(s);
	}

	public UnsupportedTransmissionFileException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedTransmissionFileException(final Throwable cause) {
		super(cause);
	}
	
}
