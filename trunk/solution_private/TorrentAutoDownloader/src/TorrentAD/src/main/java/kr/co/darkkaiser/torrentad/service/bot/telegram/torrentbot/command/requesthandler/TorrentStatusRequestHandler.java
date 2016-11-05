package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.GetTorrentImmediatelyTaskAction;

public class TorrentStatusRequestHandler extends AbstractBotCommandRequestHandler {
	
	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public TorrentStatusRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("상태", "토렌트 서버의 다운로드 상태를 조회합니다.");
		
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 0, 0) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "토렌트 서버의 다운로드 상태를 조회중입니다. 잠시만 기다려 주세요.");

		// @@@@@
		// 게시판 조회를 시작한다.
		this.immediatelyTaskExecutorService.submit(
				new GetTorrentImmediatelyTaskAction(chatRoom.incrementAndGetRequestId(), chatRoom, this.torrentBotResource, absSender));
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(TorrentStatusRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
