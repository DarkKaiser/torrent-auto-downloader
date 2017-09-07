package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

public class NoPermissionException extends IOException {

	private static final long serialVersionUID = -5548018444342604951L;

	public NoPermissionException() {
		super();
	}

	public NoPermissionException(String s) {
		super(s);
	}

	public NoPermissionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPermissionException(Throwable cause) {
		super(cause);
	}
	
}
