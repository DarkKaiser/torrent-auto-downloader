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
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardItemDownloadImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardItemDownloadRequestHandler extends AbstractRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardItemDownloadRequestHandler.class);

	private final WebSite site;

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardItemDownloadRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.INLINE_COMMAND_DOWNLOAD);

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
		if (super.executable0(command, parameters, 3, 3) == false)
			return false;
		
		if (this.site.getBoardByCode(parameters[0]) == null)
			return false;

		try {
	        Long.parseLong(parameters[1]);
	        Long.parseLong(parameters[2]);
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

			long identifier = Long.parseLong(parameters[1]);
			long downloadLinkIndex = Long.parseLong(parameters[2]);

			// 첨부파일 다운로드 시작 메시지를 사용자에게 보낸다.
			int messageId = update.getMessage().getMessageId();
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택한 첨부파일을 다운로드합니다.", messageId);

			// 첨부파일 다운로드를 시작한다.
			this.immediatelyTaskExecutorService.submit(
					new WebSiteBoardItemDownloadImmediatelyTaskAction(messageId, absSender, chatRoom, board, identifier, downloadLinkIndex, this.torrentBotResource));
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
