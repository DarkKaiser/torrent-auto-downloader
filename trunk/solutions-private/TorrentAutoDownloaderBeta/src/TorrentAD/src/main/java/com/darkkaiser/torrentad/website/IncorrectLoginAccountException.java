package com.darkkaiser.torrentad.website;

public class IncorrectLoginAccountException extends RuntimeException {

	private static final long serialVersionUID = -2407249496917325361L;

	public IncorrectLoginAccountException() {
		super();
	}
	
	public IncorrectLoginAccountException(final String s) {
		super(s);
	}
    
	public IncorrectLoginAccountException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public IncorrectLoginAccountException(final Throwable cause) {
        super(cause);
    }
    
}
