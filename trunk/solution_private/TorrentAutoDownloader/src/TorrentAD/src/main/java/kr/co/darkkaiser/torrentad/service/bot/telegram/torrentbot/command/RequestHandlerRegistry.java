package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.RequestHandler;

public interface RequestHandlerRegistry {

	boolean register(RequestHandler handler);

	boolean deregister(RequestHandler handler);

	Collection<RequestHandler> getRegisteredHandlers();
	
	RequestHandler getRegisteredHandler(String identifier);
	
	// @@@@@
	boolean execute(AbsSender absSender, Update update);
	
	// @@@@@
	RequestHandler getRequest(Update update);
	
}
