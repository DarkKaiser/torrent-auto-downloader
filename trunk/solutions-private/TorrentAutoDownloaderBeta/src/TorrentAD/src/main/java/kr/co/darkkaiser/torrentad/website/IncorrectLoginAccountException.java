package kr.co.darkkaiser.torrentad.website;

public class IncorrectLoginAccountException extends RuntimeException {

	private static final long serialVersionUID = -2407249496917325361L;

	public IncorrectLoginAccountException() {
		super();
	}
	
	public IncorrectLoginAccountException(String s) {
		super(s);
	}
    
	public IncorrectLoginAccountException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IncorrectLoginAccountException(Throwable cause) {
        super(cause);
    }
    
}
