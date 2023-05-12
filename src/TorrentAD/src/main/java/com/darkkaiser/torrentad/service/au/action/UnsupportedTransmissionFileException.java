package com.darkkaiser.torrentad.service.au.action;

public class UnsupportedTransmissionFileException extends RuntimeException {

	private static final long serialVersionUID = 5699512946466171586L;

	public UnsupportedTransmissionFileException(final String s) {
		super(s);
	}

}
