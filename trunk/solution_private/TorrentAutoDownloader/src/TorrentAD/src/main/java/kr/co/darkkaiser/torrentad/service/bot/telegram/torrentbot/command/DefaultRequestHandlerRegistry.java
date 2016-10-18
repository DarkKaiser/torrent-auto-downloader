package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;

public final class DefaultRequestHandlerRegistry implements RequestHandlerRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRequestHandlerRegistry.class);

	private final Map<String/* 식별자 */, RequestHandler> handlerMap = new LinkedHashMap<>();

	@Override
	public synchronized final boolean register(RequestHandler handler) {
		if (handler == null)
			throw new NullPointerException("handler");

		if (this.handlerMap.containsKey(handler.getIdentifier()) == true)
			return false;

		this.handlerMap.put(handler.getIdentifier(), handler);

		return true;
	}

	@Override
	public synchronized final boolean deregister(RequestHandler handler) {
		if (handler == null)
			throw new NullPointerException("handler");

		if (this.handlerMap.containsKey(handler.getIdentifier()) == true) {
			this.handlerMap.remove(handler.getIdentifier());
			return true;
		}
		
		return false;
	}

	@Override
	public synchronized final Collection<RequestHandler> getRequestHandlers() {
		return this.handlerMap.values();
	}

	@Override
	public synchronized final RequestHandler getRequestHandler(Class<?> clazz) {
		if (clazz == null)
			throw new NullPointerException("clazz");

		for (RequestHandler handler : getRequestHandlers()) {
			if (clazz.isInstance(handler) == true)
				return handler;
		}

		return null;
	}

	@Override
	public synchronized final RequestHandler getRequestHandler(Update update) {
		if (update == null)
			throw new NullPointerException("update");

		// @@@@@
		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();

	            String commandMessage = message.getText();
				String[] commandMessageArrays = commandMessage.split(BotCommandUtils.COMMAND_PARAMETER_SEPARATOR);

				String command = commandMessageArrays[0];
				if (command.startsWith(BotCommandUtils.COMMAND_INITIAL_CHARACTER) == true)
					command = command.substring(1);

				// @@@@@
				// 해당 requester로 command를 넘겨서 찾도록 한다.
				for (RequestHandler handler : getRequestHandlers()) {
//					if (handler.possibleProcess(command) == true)
//						return handler;
				}
				return this.handlerMap.get(command);
	        }
		} catch (Exception e) {
			logger.error(null, e);
		}

		return null;
	}

}
