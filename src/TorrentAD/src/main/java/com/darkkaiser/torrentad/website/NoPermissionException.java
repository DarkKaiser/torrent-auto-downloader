package com.darkkaiser.torrentad.website;

import java.io.IOException;

public class NoPermissionException extends IOException {

	private static final long serialVersionUID = -5548018444342604951L;

	public NoPermissionException() {
		super();
	}

	public NoPermissionException(final String s) {
		super(s);
	}

	public NoPermissionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoPermissionException(final Throwable cause) {
		super(cause);
	}
	
}
