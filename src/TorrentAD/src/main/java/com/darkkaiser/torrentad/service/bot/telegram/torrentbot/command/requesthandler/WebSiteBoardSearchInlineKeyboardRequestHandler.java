package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

public class WebSiteBoardSearchInlineKeyboardRequestHandler extends AbstractBotCommandRequestHandler {

	public WebSiteBoardSearchInlineKeyboardRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("$search$");
	}

	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 0, 0) == false)
			return false;

		return true;
	}

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		BotCommandUtils.answerCallbackQuery(absSender, update.getCallbackQuery().getId());
		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "[ " + chatRoom.getBoard().getDescription() + " ] 검색어를 입력하세요.");
	}

	@Override
	public String toString() {
		return WebSiteBoardSearchInlineKeyboardRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
