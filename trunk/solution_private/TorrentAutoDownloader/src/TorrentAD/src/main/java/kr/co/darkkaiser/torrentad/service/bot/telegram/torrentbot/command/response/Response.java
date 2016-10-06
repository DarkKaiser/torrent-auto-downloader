package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;

public interface Response {

	String getIdentifier();

	// @@@@@
	boolean allow(Request request);

	// @@@@@
	void cancel(AbsSender absSender, User user, Chat chat);

}
