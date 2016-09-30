package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.message.request;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

public class SearchingRequest extends AbstractRequest {

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		// @@@@@
	}
	
	@Override
	public void cancel() {
		// @@@@@
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(SearchingRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
