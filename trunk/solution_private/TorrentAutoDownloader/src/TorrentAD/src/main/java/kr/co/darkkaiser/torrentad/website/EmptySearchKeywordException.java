package kr.co.darkkaiser.torrentad.website;

public class EmptySearchKeywordException extends RuntimeException {

	private static final long serialVersionUID = -4502267990254247102L;

	public EmptySearchKeywordException() {
		super();
	}

	public EmptySearchKeywordException(String s) {
		super(s);
	}

	public EmptySearchKeywordException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptySearchKeywordException(Throwable cause) {
		super(cause);
	}
	
}
