package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardListResultDownloadLinkInquiryImmediatelyTaskAction;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.website.WebSite;

public class WebSiteBoardListResultDownloadLinkInquiryRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardListResultDownloadLinkInquiryRequestHandler.class);

	private final WebSite site;

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardListResultDownloadLinkInquiryRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.LASR_LIST_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND);

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
		if (super.executable0(command, parameters, containInitialChar, 2, 2) == false)
			return false;

		if (this.site.getBoardByCode(parameters[0]) == null)
			return false;

		if (StringUtil.isNumeric(parameters[1]) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		try {
			WebSiteBoard board = this.site.getBoardByCode(parameters[0]);
			if (board == null)
				throw new NullPointerException("board");

			long identifier = Long.parseLong(parameters[1]);

			// 선택된 게시물의 첨부파일 확인중 메시지를 사용자에게 보낸다.
			int messageId = update.getMessage().getMessageId();
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택된 게시물의 첨부파일을 확인중입니다...", messageId);

			// 첨부파일 조회를 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardListResultDownloadLinkInquiryImmediatelyTaskAction(messageId, absSender, chatRoom, board, identifier, this.torrentBotResource));
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardListResultDownloadLinkInquiryRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}