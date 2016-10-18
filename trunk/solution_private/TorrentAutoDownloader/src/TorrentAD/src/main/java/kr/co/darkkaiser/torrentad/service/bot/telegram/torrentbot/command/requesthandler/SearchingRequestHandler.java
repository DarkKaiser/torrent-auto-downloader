package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

public class SearchingRequestHandler extends AbstractRequestHandler {
	
	// @@@@@
	public SearchingRequestHandler() {
		super("search");
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		// @@@@@
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
