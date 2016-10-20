package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;

public final class DefaultRequestHandlerRegistry implements RequestHandlerRegistry {
	
	@SuppressWarnings("unused")
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
	public synchronized final RequestHandler getRequestHandler(String command, String[] parameters) {
		if (StringUtil.isBlank(command) == true)
			return null;

		for (RequestHandler handler : getRequestHandlers()) {
			// @@@@@
			// 해당 requester로 command를 넘겨서 찾도록 한다.
//			if (handler.possibleProcess(outCommand.get(), outParameters.get()) == true)
//				return handler;
			return this.handlerMap.get(command);
		}
		
		return null;
	}

}
