package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public class SearchingRequestHandler extends AbstractRequestHandler {
	
	// @@@@@
	public SearchingRequestHandler() {
		super("search");
	}

	@Override
	public Response execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		// @@@@@
		return null;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(SearchingRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
