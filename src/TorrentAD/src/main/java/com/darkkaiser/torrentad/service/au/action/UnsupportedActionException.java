package com.darkkaiser.torrentad.service.au.action;

import java.io.Serial;

public class UnsupportedActionException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 2101408061616234747L;

	public UnsupportedActionException(final String s) {
		super(s);
	}

}
