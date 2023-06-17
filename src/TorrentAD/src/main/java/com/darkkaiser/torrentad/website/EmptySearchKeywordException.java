package com.darkkaiser.torrentad.website;

import java.io.Serial;

public class EmptySearchKeywordException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -4657183351987411925L;

	public EmptySearchKeywordException(final String s) {
		super(s);
	}

}
