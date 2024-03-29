package com.darkkaiser.torrentad.website;

import java.io.IOException;
import java.io.Serial;

public class LoadBoardItemsException extends IOException {

	@Serial
    private static final long serialVersionUID = -6224782226170936673L;

	public LoadBoardItemsException() {
		super();
	}

	public LoadBoardItemsException(final String s) {
		super(s);
	}

	public LoadBoardItemsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public LoadBoardItemsException(final Throwable cause) {
		super(cause);
	}
	
}
