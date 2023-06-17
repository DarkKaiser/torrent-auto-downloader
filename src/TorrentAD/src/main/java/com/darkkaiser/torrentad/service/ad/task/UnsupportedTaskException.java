package com.darkkaiser.torrentad.service.ad.task;

import java.io.Serial;

public class UnsupportedTaskException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 7317989810880510772L;
	
	public UnsupportedTaskException(final String s) {
		super(s);
	}

}
