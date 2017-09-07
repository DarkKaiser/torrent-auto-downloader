package kr.co.darkkaiser.torrentad.website;

import java.io.IOException;

public class LoadBoardItemsException extends IOException {

	private static final long serialVersionUID = -6224782226170936673L;

	public LoadBoardItemsException() {
		super();
	}

	public LoadBoardItemsException(String s) {
		super(s);
	}

	public LoadBoardItemsException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadBoardItemsException(Throwable cause) {
		super(cause);
	}
	
}
