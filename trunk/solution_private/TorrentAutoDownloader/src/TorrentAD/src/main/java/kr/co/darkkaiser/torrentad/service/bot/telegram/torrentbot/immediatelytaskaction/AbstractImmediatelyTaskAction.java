package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;

public abstract class AbstractImmediatelyTaskAction extends kr.co.darkkaiser.torrentad.service.ad.task.immediately.AbstractImmediatelyTaskAction {

	protected void sendMessage(AbsSender absSender, long chatId, String message) {
		BotCommandUtils.sendMessage(absSender, chatId, message);
	}

	@Override
	public void validate() {
		super.validate();
	}

}
