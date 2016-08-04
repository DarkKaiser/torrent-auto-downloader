package kr.co.darkkaiser.torrentad.website;

public class InvalidWebSiteAccountException extends RuntimeException {

	private static final long serialVersionUID = 5696303620528292803L;

	public InvalidWebSiteAccountException() {
		super();
	}
	
	public InvalidWebSiteAccountException(String s) {
		super(s);
	}
    
	public InvalidWebSiteAccountException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidWebSiteAccountException(Throwable cause) {
        super(cause);
    }

}
