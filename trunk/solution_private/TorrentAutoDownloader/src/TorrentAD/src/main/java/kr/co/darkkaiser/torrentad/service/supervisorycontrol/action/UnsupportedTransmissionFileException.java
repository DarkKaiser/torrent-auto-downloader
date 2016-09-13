package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public class UnsupportedTransmissionFileException extends RuntimeException {

	private static final long serialVersionUID = 5699512946466171586L;

	public UnsupportedTransmissionFileException() {
		super();
	}

	public UnsupportedTransmissionFileException(String s) {
		super(s);
	}

	public UnsupportedTransmissionFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedTransmissionFileException(Throwable cause) {
		super(cause);
	}
	
}
