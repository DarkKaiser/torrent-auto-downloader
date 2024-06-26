package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardListResultDownloadLinkInquiryImmediatelyTaskAction;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Objects;

@Slf4j
public class WebSiteBoardListResultDownloadLinkInquiryRequestHandler extends AbstractBotCommandRequestHandler {

	private final WebSite site;

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardListResultDownloadLinkInquiryRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.LASR_LIST_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND);

		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(torrentBotResource.getSite(), "site");
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");

		this.site = torrentBotResource.getSite();
		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}

	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 2, 2) == false)
			return false;

		if (this.site.getBoardByCode(parameters[0]) == null)
			return false;

        return StringUtil.isNumeric(parameters[1]) != false;
    }

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		try {
			WebSiteBoard board = Objects.requireNonNull(this.site.getBoardByCode(parameters[0]), "board");

			long identifier = Long.parseLong(parameters[1]);

			// 선택된 게시물의 첨부파일 확인중 메시지를 사용자에게 보낸다.
			int messageId = update.getMessage().getMessageId();
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택된 게시물의 첨부파일을 확인중입니다...", messageId);

			// 첨부파일 조회를 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardListResultDownloadLinkInquiryImmediatelyTaskAction(messageId, absSender, chatRoom, board, identifier, this.torrentBotResource));
		} catch (final Exception e) {
			log.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return WebSiteBoardListResultDownloadLinkInquiryRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
