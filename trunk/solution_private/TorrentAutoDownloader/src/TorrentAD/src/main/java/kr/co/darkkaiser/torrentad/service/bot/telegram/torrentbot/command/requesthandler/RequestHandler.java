package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

public interface RequestHandler {

	String getIdentifier();

	boolean executable(String command, String[] parameters, boolean containInitialChar);

	void execute(AbsSender absSender, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar, Update update);

}
