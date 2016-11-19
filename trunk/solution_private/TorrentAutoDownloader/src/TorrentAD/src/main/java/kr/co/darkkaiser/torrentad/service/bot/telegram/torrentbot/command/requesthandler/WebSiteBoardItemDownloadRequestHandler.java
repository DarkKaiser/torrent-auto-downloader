package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardItemDownloadImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardItemDownloadRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardItemDownloadRequestHandler.class);

	private final WebSite site;

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	private final FileTransmissionExecutorService fileTransmissionExecutorService;
	
	public WebSiteBoardItemDownloadRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService, FileTransmissionExecutorService fileTransmissionExecutorService) {
		super(BotCommandConstants.DOWNLOAD_REQUEST_INLINE_COMMAND);

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (fileTransmissionExecutorService == null)
			throw new NullPointerException("fileTransmissionExecutorService");

		this.site = torrentBotResource.getSite();
		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
		this.fileTransmissionExecutorService = fileTransmissionExecutorService;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 3, 3) == false)
			return false;

		if (this.site.getBoardByCode(parameters[0]) == null)
			return false;
		
		if (StringUtil.isNumeric(parameters[1]) == false || StringUtil.isNumeric(parameters[2]) == false)
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
			long downloadLinkIndex = Long.parseLong(parameters[2]);

			// 첨부파일 다운로드 시작 메시지를 사용자에게 보낸다.
			int messageId = update.getMessage().getMessageId();
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택한 첨부파일을 다운로드합니다. 다운로드 작업은 10초정도 소요됩니다.", messageId);

			// 첨부파일 다운로드를 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardItemDownloadImmediatelyTaskAction(messageId, absSender, chatRoom, board, identifier, downloadLinkIndex, this.torrentBotResource, this.fileTransmissionExecutorService));
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}

	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardItemDownloadRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
