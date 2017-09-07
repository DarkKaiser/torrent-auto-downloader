package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;

public interface RequestHandlerRegistry {

	boolean register(RequestHandler handler);

	boolean deregister(RequestHandler handler);

	Collection<RequestHandler> getRequestHandlers();

	RequestHandler getRequestHandler(Class<?> clazz);

	RequestHandler getRequestHandler(String command, String[] parameters, boolean containInitialChar);

}
