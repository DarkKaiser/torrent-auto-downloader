package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;

@Slf4j
@Getter
public abstract class AbstractRequestHandler implements RequestHandler {
	
	private final String identifier;

	public AbstractRequestHandler(final String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		this.identifier = identifier;
	}

	protected abstract boolean executable0(final String command, final String[] parameters, final boolean containInitialChar, final int minParametersCount, final int maxParametersCount);

	protected void logError(final String message, final String command, final String[] parameters, final boolean containInitialChar) {
		StringBuilder sbLogMessage = new StringBuilder()
				.append(message).append("(")
				.append("command:").append(command)
				.append(", parameters:[");

		if (parameters != null && parameters.length > 0) {
			for (final String parameter : parameters)
				sbLogMessage.append(parameter).append(",");

			sbLogMessage.delete(sbLogMessage.length() - 1, sbLogMessage.length());
		}

		sbLogMessage.append("]")
				    .append(", containInitialChar:").append(containInitialChar).append(")");

		log.error(sbLogMessage.toString());
	}

	@Override
	public String toString() {
		return AbstractRequestHandler.class.getSimpleName() +
				"{" +
				"identifier:" + getIdentifier() +
				"}";
	}

}
