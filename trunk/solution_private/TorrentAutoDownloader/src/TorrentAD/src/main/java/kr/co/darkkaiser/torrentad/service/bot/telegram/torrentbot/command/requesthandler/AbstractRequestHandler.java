package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestHandler implements RequestHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractRequestHandler.class);
	
	private final String identifier;

	public AbstractRequestHandler(String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		this.identifier = identifier;
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	protected abstract boolean executable0(String command, String[] parameters, boolean containInitialChar, int minParametersCount, int maxParametersCount);

	protected void logError(String message, String command, String[] parameters, boolean containInitialChar) {
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
		return new StringBuilder()
				.append(AbstractRequestHandler.class.getSimpleName())
				.append("{")
				.append("identifier:").append(getIdentifier())
				.append("}")
				.toString();
	}

}
