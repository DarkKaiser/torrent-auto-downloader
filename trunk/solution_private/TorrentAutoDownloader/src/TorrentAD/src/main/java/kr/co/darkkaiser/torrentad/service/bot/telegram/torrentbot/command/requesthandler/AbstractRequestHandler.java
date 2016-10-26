package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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

	protected void sendAnswerMessage(AbsSender absSender, long chatId, String message) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");

		SendMessage answerMessage = new SendMessage()
				.setChatId(Long.toString(chatId))
				.setText(message)
				.enableHtml(true);

		try {
			absSender.sendMessage(answerMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
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
