package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardDownloadLinkListImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardDownloadLinkListRequestHandler extends AbstractRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardDownloadLinkListRequestHandler.class);

	private final WebSite site;

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardDownloadLinkListRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.LASR_DOWNLOAD_LINK_LIST_INLINE_COMMAND);

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
		if (super.executable0(command, parameters, 2, 2) == false)
			return false;

		if (this.site.getBoardByCode(parameters[0]) == null)
			return false;

		try {
	        Long.parseLong(parameters[1]);
	    } catch (NumberFormatException e) {
	        return false;
	    }

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		try {
			WebSiteBoard board = this.site.getBoardByCode(parameters[0]);
			if (board == null)
				throw new NullPointerException("board");

			long identifierValue = Long.parseLong(parameters[1]);
		
			// @@@@@ message

			// 게시판 조회를 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardDownloadLinkListImmediatelyTaskAction(absSender, chatRoom, board, identifierValue, this.torrentBotResource));
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardDownloadLinkListRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
