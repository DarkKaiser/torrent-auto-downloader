package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.AbstractImmediatelyTaskAction;

// @@@@@ 클래스명
public abstract class AbstractTorrentBotImmediatelyTaskAction extends AbstractImmediatelyTaskAction {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTorrentBotImmediatelyTaskAction.class);

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
	public void validate() {
		super.validate();
	}

}
