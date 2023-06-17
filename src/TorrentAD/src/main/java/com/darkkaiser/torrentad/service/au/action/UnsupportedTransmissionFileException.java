package com.darkkaiser.torrentad.service.au.action;

import java.io.Serial;

public class UnsupportedTransmissionFileException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 5699512946466171586L;

	public UnsupportedTransmissionFileException(final String s) {
		super(s);
	}

}
