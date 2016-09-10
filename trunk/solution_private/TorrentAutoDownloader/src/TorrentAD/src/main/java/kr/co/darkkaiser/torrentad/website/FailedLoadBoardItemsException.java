package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

public class FailedLoadBoardItemsException extends IOException {

	private static final long serialVersionUID = -6224782226170936673L;

	public FailedLoadBoardItemsException() {
		super();
	}

	public FailedLoadBoardItemsException(String s) {
		super(s);
	}

	public FailedLoadBoardItemsException(String message, Throwable cause) {
		super(message, cause);
	}

	public FailedLoadBoardItemsException(Throwable cause) {
		super(cause);
	}
	
}
