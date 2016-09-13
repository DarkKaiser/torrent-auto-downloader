package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public class UnsupportedActionException extends RuntimeException {

	private static final long serialVersionUID = 2101408061616234747L;

	public UnsupportedActionException() {
		super();
	}

	public UnsupportedActionException(String s) {
		super(s);
	}

	public UnsupportedActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedActionException(Throwable cause) {
		super(cause);
	}

}
