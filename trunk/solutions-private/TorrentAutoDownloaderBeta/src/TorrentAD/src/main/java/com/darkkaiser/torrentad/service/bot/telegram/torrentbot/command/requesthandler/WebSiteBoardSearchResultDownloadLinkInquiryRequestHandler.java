package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardSearchResultDownloadLinkInquiryImmediatelyTaskAction;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Objects;

public class WebSiteBoardSearchResultDownloadLinkInquiryRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardSearchResultDownloadLinkInquiryRequestHandler.class);

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardSearchResultDownloadLinkInquiryRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.LASR_SEARCH_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND);

		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}

	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 2, 2) == false)
			return false;

		if (StringUtil.isNumeric(parameters[1]) == false)
			return false;

		return true;
	}

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		try {
			// 선택된 게시물의 첨부파일 확인중 메시지를 사용자에게 보낸다.
			int messageId = update.getMessage().getMessageId();
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택된 게시물의 첨부파일을 확인중입니다...", messageId);

			// 첨부파일 조회를 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardSearchResultDownloadLinkInquiryImmediatelyTaskAction(messageId, absSender, chatRoom, parameters[0], Long.parseLong(parameters[1]), this.torrentBotResource));
		} catch (final Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return WebSiteBoardSearchResultDownloadLinkInquiryRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
