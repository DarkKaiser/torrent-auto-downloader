package kr.co.darkkaiser.torrentad.website;

public class EmptySearchKeywordsException extends RuntimeException {

	private static final long serialVersionUID = -4502267990254247102L;

	public EmptySearchKeywordsException() {
		super();
	}

	public EmptySearchKeywordsException(String s) {
		super(s);
	}

	public EmptySearchKeywordsException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptySearchKeywordsException(Throwable cause) {
		super(cause);
	}
	
}
