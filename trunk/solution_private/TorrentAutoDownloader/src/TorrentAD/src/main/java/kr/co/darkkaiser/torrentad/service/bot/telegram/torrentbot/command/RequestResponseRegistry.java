package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public interface RequestResponseRegistry {

	boolean register(Request request);

	boolean deregister(Request request);

	Collection<Request> getRegisteredRequests();
	
	Request getRegisteredRequest(String identifier);

	boolean register(Response response);

	boolean deregister(Response response);

	Collection<Response> getRegisteredResponses();
	
	Response getRegisteredResponse(String identifier);
	
	boolean execute(AbsSender absSender, Update update);
	
	Request get(Update update);
	
}
