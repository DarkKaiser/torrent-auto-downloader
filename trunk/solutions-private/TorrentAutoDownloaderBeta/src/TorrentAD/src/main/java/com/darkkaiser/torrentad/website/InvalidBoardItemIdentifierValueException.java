package com.darkkaiser.torrentad.website;

public class InvalidBoardItemIdentifierValueException extends RuntimeException {

	private static final long serialVersionUID = 2734563956539886557L;

	public InvalidBoardItemIdentifierValueException() {
		super();
	}
	
	public InvalidBoardItemIdentifierValueException(final String s) {
		super(s);
	}
    
	public InvalidBoardItemIdentifierValueException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public InvalidBoardItemIdentifierValueException(final Throwable cause) {
        super(cause);
    }
    
}
