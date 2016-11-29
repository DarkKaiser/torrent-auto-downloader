package kr.co.darkkaiser.torrentad.website;

public class InvalidBoardItemIdentifierValueException extends RuntimeException {

	private static final long serialVersionUID = 2734563956539886557L;

	public InvalidBoardItemIdentifierValueException() {
		super();
	}
	
	public InvalidBoardItemIdentifierValueException(String s) {
		super(s);
	}
    
	public InvalidBoardItemIdentifierValueException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidBoardItemIdentifierValueException(Throwable cause) {
        super(cause);
    }
    
}
