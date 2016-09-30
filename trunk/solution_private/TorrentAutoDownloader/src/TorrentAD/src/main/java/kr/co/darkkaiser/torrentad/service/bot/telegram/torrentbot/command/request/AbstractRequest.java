package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

public abstract class AbstractRequest implements Request {
	
	private final String identifier;
	
	public AbstractRequest(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() {
		// @@@@@
		return this.identifier;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractRequest.class.getSimpleName())
				.append("{")
				.append("}")
				.toString();
	}

}
