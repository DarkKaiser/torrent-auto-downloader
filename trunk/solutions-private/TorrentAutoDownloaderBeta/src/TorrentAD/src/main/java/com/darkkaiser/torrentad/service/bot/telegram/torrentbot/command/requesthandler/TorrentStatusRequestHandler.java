package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.ExposedBotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.TorrentStatusImmediatelyTaskAction;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

public class TorrentStatusRequestHandler extends AbstractBotCommandRequestHandler implements ExposedBotCommand {
	
	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public TorrentStatusRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("status", "상태", "/status (상태)", "토렌트 서버의 상태를 조회합니다.");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}

	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		return super.executable0(command, parameters, containInitialChar, 0, 0) != false;
	}

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "토렌트 서버의 상태를 조회중입니다...");

		// 토렌트 상태 조회를 시작한다.
		this.immediatelyTaskExecutorService.submit(
				new TorrentStatusImmediatelyTaskAction(absSender, chatRoom, this.torrentBotResource));
	}

	@Override
	public String toString() {
		return TorrentStatusRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
