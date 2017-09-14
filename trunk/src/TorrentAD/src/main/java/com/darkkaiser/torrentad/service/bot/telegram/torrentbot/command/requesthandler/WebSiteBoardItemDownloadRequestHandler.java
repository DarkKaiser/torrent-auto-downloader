package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardItemDownloadImmediatelyTaskAction;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Objects;

public class WebSiteBoardItemDownloadRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardItemDownloadRequestHandler.class);

	private final WebSite site;

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	private final FileTransmissionExecutorService fileTransmissionExecutorService;
	
	public WebSiteBoardItemDownloadRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService, final FileTransmissionExecutorService fileTransmissionExecutorService) {
		super(BotCommandConstants.DOWNLOAD_REQUEST_INLINE_COMMAND);

		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(torrentBotResource.getSite(), "site");
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");
		Objects.requireNonNull(fileTransmissionExecutorService, "fileTransmissionExecutorService");

		this.site = torrentBotResource.getSite();
		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
		this.fileTransmissionExecutorService = fileTransmissionExecutorService;
	}
	
	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 3, 3) == false)
			return false;

		return StringUtil.isNumeric(parameters[1]) != false && StringUtil.isNumeric(parameters[2]) != false;
	}

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		try {
			// 첨부파일 다운로드 시작 메시지를 사용자에게 보낸다.
			int messageId = update.getMessage().getMessageId();
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택한 첨부파일을 다운로드합니다. 다운로드 작업은 10초정도 소요됩니다.", messageId);

			long identifier = Long.parseLong(parameters[1]);
			long downloadLinkIndex = Long.parseLong(parameters[2]);

			// 첨부파일 다운로드를 시작한다.
			WebSiteBoard board = this.site.getBoardByCode(parameters[0]);
			if (board != null) {
				this.immediatelyTaskExecutorService.submit(
						new WebSiteBoardItemDownloadImmediatelyTaskAction(messageId, absSender, chatRoom, board, identifier, downloadLinkIndex, this.torrentBotResource, this.fileTransmissionExecutorService));
			} else {
				this.immediatelyTaskExecutorService.submit(
						new WebSiteBoardItemDownloadImmediatelyTaskAction(messageId, absSender, chatRoom, parameters[0], identifier, downloadLinkIndex, this.torrentBotResource, this.fileTransmissionExecutorService));
			}
		} catch (final Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}

	}

	@Override
	public String toString() {
		return WebSiteBoardItemDownloadRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
