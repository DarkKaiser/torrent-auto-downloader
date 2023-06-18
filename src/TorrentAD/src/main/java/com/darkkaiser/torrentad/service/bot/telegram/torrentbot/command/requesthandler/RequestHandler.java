package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

public interface RequestHandler {

	String getIdentifier();

	boolean executable(final String command, final String[] parameters, final boolean containInitialChar);

	void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar);

}
