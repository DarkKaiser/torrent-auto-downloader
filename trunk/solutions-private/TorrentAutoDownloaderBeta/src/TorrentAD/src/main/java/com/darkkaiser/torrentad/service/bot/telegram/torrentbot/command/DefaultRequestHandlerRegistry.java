package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class DefaultRequestHandlerRegistry implements RequestHandlerRegistry {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DefaultRequestHandlerRegistry.class);

	private final Map<String/* 식별자 */, RequestHandler> handlerMap = new LinkedHashMap<>();

	@Override
	public synchronized final boolean register(final RequestHandler handler) {
		Objects.requireNonNull(handler, "handler");

		if (this.handlerMap.containsKey(handler.getIdentifier()) == true)
			return false;

		this.handlerMap.put(handler.getIdentifier(), handler);

		return true;
	}

	@Override
	public synchronized final boolean deregister(final RequestHandler handler) {
		Objects.requireNonNull(handler, "handler");

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
	public synchronized final RequestHandler getRequestHandler(final Class<?> clazz) {
		Objects.requireNonNull(clazz, "clazz");

		for (final RequestHandler handler : getRequestHandlers()) {
			if (clazz.isInstance(handler) == true)
				return handler;
		}

		return null;
	}

	@Override
	public synchronized final RequestHandler getRequestHandler(final String command, final String[] parameters, final boolean containInitialChar) {
		if (StringUtil.isBlank(command) == true)
			return null;

		// 주어진 명령을 실행할 수 있는 RequestHandler를 찾아서 반환한다.
		for (final RequestHandler handler : getRequestHandlers()) {
			if (handler.executable(command, parameters, containInitialChar) == true)
				return handler;
		}

		return null;
	}

}
