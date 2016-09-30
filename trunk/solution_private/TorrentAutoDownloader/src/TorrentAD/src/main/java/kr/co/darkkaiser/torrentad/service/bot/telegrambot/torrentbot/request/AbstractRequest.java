package kr.co.darkkaiser.torrentad.service.bot.telegrambot.torrentbot.request;

// @@@@@
public abstract class AbstractRequest implements Request {

	private final String identifier;
	private final String description;

	public AbstractRequest(String identifier, String description) {
		this.identifier = identifier;
		this.description = description;
	}

	public String getIdentifier() {
		return this.identifier;
	}
	
	public String getDescription() {
		return this.description;
	}

	// @Override
	// public String toString() {
	// return "<b>" + getCommandIdentifier() + "</b>\n" + getDescription();
	// }

}
