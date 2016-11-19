package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.ExposedBotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardSearchImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardSearchRequestHandler extends AbstractBotCommandRequestHandler implements ExposedBotCommand {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardSearchRequestHandler.class);

	private final WebSite site;
	
	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;

	public WebSiteBoardSearchRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("search", "검색", "/search (검색) [검색어]\n/search (검색) [게시판] [검색어]", "선택된 게시판을 검색합니다.");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.site = torrentBotResource.getSite();
		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 1, 2) == false)
			return false;

		if (parameters.length == 2) {
			if (this.site.getBoardByCode(parameters[0]) == null)
				return false;
		}

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		try {
			String keyword;
			WebSiteBoard board = chatRoom.getBoard();
			if (parameters.length == 2) {
				keyword = parameters[1];
				board = this.site.getBoardByCode(parameters[0]);
				if (board == null)
					throw new NullPointerException("board");

				chatRoom.setBoard(board);
			} else {
				keyword = parameters[0];
			}
			
			// 게시판 검색중 메시지를 사용자에게 보낸다.
			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판을 검색중입니다.");

			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());

			// 게시판 검색을 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardSearchImmediatelyTaskAction(chatRoom.incrementAndGetRequestId(), absSender, chatRoom, board, keyword, this.torrentBotResource));
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardSearchRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
