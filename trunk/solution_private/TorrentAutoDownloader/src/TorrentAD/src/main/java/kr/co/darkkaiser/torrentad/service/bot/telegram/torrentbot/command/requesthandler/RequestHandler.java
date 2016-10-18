package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

public interface RequestHandler {

	String getIdentifier();

	// @@@@@
	void execute(AbsSender absSender, User user, Chat chat, String[] arguments);

}
