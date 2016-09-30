package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response;

public abstract class AbstractResponse implements Response {

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractResponse.class.getSimpleName())
				.append("{")
				.append("}")
				.toString();
	}

}
