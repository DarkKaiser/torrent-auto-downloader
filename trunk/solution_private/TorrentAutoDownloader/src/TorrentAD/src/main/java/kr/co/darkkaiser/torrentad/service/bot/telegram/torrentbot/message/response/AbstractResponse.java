package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.message.response;

// @@@@@
public abstract class AbstractResponse implements Response {

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractResponse.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
