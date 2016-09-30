package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

public abstract class AbstractRequest implements Request {
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
