package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

public interface RequestHandler {

	String getIdentifier();

	boolean executable(String command, String[] parameters, boolean containInitialChar);

	void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar);

}
