package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.ExposedBotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardSearchImmediatelyTaskAction;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardSearchRequestHandler extends AbstractBotCommandRequestHandler implements ExposedBotCommand {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardSearchRequestHandler.class);

	private final WebSite site;
	
	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	private final RequestHandlerRegistry requestHandlerRegistry;

	public WebSiteBoardSearchRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService, RequestHandlerRegistry requestHandlerRegistry) {
		super("search", "검색", "/search (검색) [검색어]\n/search (검색) [게시판] [검색어]", "선택된 게시판을 검색합니다.");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.site = torrentBotResource.getSite();
		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
		this.requestHandlerRegistry = requestHandlerRegistry;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 0, -1) == false)
			return false;

		// '검색 [게시판]' 형태(검색어가 입력되지 않은 형태)로 입력되면, false를 반환한다.
		if (parameters.length == 1) {
			if (this.site.getBoardByCode(parameters[0]) != null)
				return false;
		}
		
		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		try {
			String keyword;
			WebSiteBoard board = chatRoom.getBoard();

			// 검색어를 추출한다.
			if (parameters.length >= 2) {
				WebSiteBoard inBoard = this.site.getBoardByCode(parameters[0]);
				if (inBoard == null) {
					StringBuilder sbKeyword = new StringBuilder();
					for (int index = 0; index < parameters.length; ++index) {
						if (sbKeyword.length() > 0)
							sbKeyword.append(BotCommandConstants.BOT_COMMAND_PARAMETER_SEPARATOR);
						
						sbKeyword.append(parameters[index]);
					}

					keyword = sbKeyword.toString();
				} else {
					board = inBoard;
					chatRoom.setBoard(board);
					
					StringBuilder sbKeyword = new StringBuilder();
					for (int index = 1; index < parameters.length; ++index) {
						if (sbKeyword.length() > 0)
							sbKeyword.append(BotCommandConstants.BOT_COMMAND_PARAMETER_SEPARATOR);

						sbKeyword.append(parameters[index]);
					}

					keyword = sbKeyword.toString();
				}
			} else if (parameters.length == 1) {
				keyword = parameters[0];
			} else {
				BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), new StringBuilder().append("[ ").append(chatRoom.getBoard().getDescription()).append(" ] 검색어를 입력하세요.").toString());
				
				// '검색' 혹은 '/select'만 입력된 경우 이후에 검색어를 입력하였을 때 검색될 수 있도록 RequestHandler를 조정한다.
				chatRoom.setLatestRequestHandler(this.requestHandlerRegistry.getRequestHandler(WebSiteBoardSearchInlineKeyboardRequestHandler.class));

				return;
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
