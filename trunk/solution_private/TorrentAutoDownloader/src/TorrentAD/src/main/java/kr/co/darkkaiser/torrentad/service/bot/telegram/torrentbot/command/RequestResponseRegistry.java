package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public interface RequestResponseRegistry {

	boolean register(Request request);

	boolean register(Response response);
	
	boolean deregister(Request request);

	boolean deregister(Response response);

	Collection<Request> getRegisteredRequests();
	
	Collection<Response> getRegisteredResponses();

	Request getRegisteredRequest(String identifier);
	
	Response getRegisteredResponse(String identifier);

	// @@@@@
	boolean execute(AbsSender absSender, Update update);
	
	// @@@@@
	Request get(Update update);
	
}
