package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;

public interface RequestHandlerRegistry {

	boolean register(final RequestHandler handler);

	boolean deregister(final RequestHandler handler);

	Collection<RequestHandler> getRequestHandlers();

	RequestHandler getRequestHandler(final Class<?> clazz);

	RequestHandler getRequestHandler(final String command, final String[] parameters, final boolean containInitialChar);

}
