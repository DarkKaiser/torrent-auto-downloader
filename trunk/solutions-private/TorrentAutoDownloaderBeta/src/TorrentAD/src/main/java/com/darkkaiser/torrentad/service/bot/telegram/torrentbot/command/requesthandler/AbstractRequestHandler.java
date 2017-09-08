package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestHandler implements RequestHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractRequestHandler.class);
	
	private final String identifier;

	public AbstractRequestHandler(final String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		this.identifier = identifier;
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	protected abstract boolean executable0(final String command, final String[] parameters, final boolean containInitialChar, final int minParametersCount, final int maxParametersCount);

	@SuppressWarnings("SameParameterValue")
	protected void logError(final String message, final String command, final String[] parameters, final boolean containInitialChar) {
		StringBuilder sbLogMessage = new StringBuilder()
				.append(message).append("(")
				.append("command:").append(command)
				.append(", parameters:[");

		if (parameters != null && parameters.length > 0) {
			for (String parameter : parameters)
				sbLogMessage.append(parameter).append(",");

			sbLogMessage.delete(sbLogMessage.length() - 1, sbLogMessage.length());
		}

		sbLogMessage.append("]")
				.append(", containInitialChar:").append(containInitialChar).append(")");

		logger.error(sbLogMessage.toString());
	}

	@Override
	public String toString() {
		return AbstractRequestHandler.class.getSimpleName() +
				"{" +
				"identifier:" + getIdentifier() +
				"}";
	}

}
