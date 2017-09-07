package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public synchronized final RequestHandler getRequestHandler(String command, String[] parameters, boolean containInitialChar) {
		if (StringUtil.isBlank(command) == true)
			return null;

		// 주어진 명령을 실행할 수 있는 RequestHandler를 찾아서 반환한다.
		for (RequestHandler handler : getRequestHandlers()) {
			if (handler.executable(command, parameters, containInitialChar) == true)
				return handler;
		}

		return null;
	}

}
