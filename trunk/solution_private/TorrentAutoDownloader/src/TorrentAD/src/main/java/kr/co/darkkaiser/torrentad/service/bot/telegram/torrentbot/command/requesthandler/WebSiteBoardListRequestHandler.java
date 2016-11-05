package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardListImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardListRequestHandler extends AbstractBotCommandRequestHandler {

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardListRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("조회", "선택된 게시판을 조회합니다.");

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
		// 조회할 게시판이 선택되었는지 확인한다.
		WebSiteBoard board = chatRoom.getBoard();
		if (board == null) {
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "조회 및 검색하려는 게시판을 먼저 선택하세요.");
			return;
		}

		// 게시판 조회중 메시지를 사용자에게 보낸다.
		StringBuilder sbAnswerMessage = new StringBuilder()
				.append("[ ").append(board.getDescription()).append(" ] 게시판을 조회중입니다. 잠시만 기다려 주세요.");

		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());

		// @@@@@
		// 게시판 조회를 시작한다.
		this.immediatelyTaskExecutorService.submit(
				new WebSiteBoardListImmediatelyTaskAction(chatRoom.incrementAndGetRequestId(), board, chatRoom, this.torrentBotResource, absSender, -1));
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardListRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
