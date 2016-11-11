package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.util.Tuple;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class WebSiteBoardItemDownloadImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardItemDownloadImmediatelyTaskAction.class);
	
	private final int messageId;

	private final AbsSender absSender;

	private final ChatRoom chatRoom;

	private final WebSiteBoard board;
	
	private final long boardItemIdentifier;
	
	private final long boardItemDownloadLinkIndex;

	private final FileTransmissionExecutorService fileTransmissionExecutorService;

	private final WebSite site;

	private final WebSiteHandler siteHandler;

	public WebSiteBoardItemDownloadImmediatelyTaskAction(int messageId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, long boardItemIdentifier, long boardItemDownloadLinkIndex, 
			TorrentBotResource torrentBotResource, FileTransmissionExecutorService fileTransmissionExecutorService) {

		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (board == null)
			throw new NullPointerException("board");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (torrentBotResource.getSiteConnector() == null)
			throw new NullPointerException("siteConnector");
		if (torrentBotResource.getSiteConnector().getConnection() == null)
			throw new NullPointerException("siteConnection");
		if (fileTransmissionExecutorService == null)
			throw new NullPointerException("fileTransmissionExecutorService");

		this.messageId = messageId;
		
		this.absSender = absSender;
		this.chatRoom = chatRoom;
		
		this.board = board;
		this.boardItemIdentifier = boardItemIdentifier;
		this.boardItemDownloadLinkIndex = boardItemDownloadLinkIndex;
		
		this.site = torrentBotResource.getSite();
		this.siteHandler = (WebSiteHandler) torrentBotResource.getSiteConnector().getConnection();

		this.fileTransmissionExecutorService = fileTransmissionExecutorService;
	}

	@Override
	public String getName() {
		return String.format("%s > %s > %d > %d 첨부파일 다운로드", this.site.getName(), this.board.getDescription(), this.boardItemIdentifier, this.boardItemDownloadLinkIndex);
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// 선택된 게시판을 조회한다.
			Iterator<WebSiteBoardItem> iterator = this.siteHandler.list(this.board, false, new WebSiteBoardItemComparatorIdentifierDesc());
			while (iterator.hasNext() == true) {
				// 사용자가 선택한 게시물을 찾는다.
				WebSiteBoardItem boardItem = iterator.next();
				if (boardItem.getIdentifier() != this.boardItemIdentifier)
					continue;

				Tuple<Integer, Integer> tuple = this.siteHandler.download(boardItem, this.boardItemDownloadLinkIndex);
				
				if (tuple.first() < 0 && tuple.last() < 0) {
					BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "선택한 첨부파일에 대한 정보를 찾을 수 없습니다. 다운로드가 실패하였습니다.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.", this.messageId);
				} else if (tuple.first() == 0 && tuple.last() == 0) {
					BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "선택한 첨부파일에 대한 정보를 찾을 수 없습니다. 다운로드가 실패하였습니다.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.", this.messageId);
				} else if (tuple.first() == 1 && tuple.last() == 0) {
					BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "선택한 첨부파일의 다운로드가 실패하였습니다. 다시 시도하여 주세요.", this.messageId);
				} else if (tuple.first() == 1 && tuple.last() == 1) {
					this.fileTransmissionExecutorService.submit();
				}

				return true;
			}

			// 선택한 게시물을 찾을 수 없는 경우, 사용자에게 에러 메시지를 보낸다.
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "해당 게시물을 찾을 수 없습니다. 조회 또는 검색을 다시 시도하여 주세요.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.", this.messageId);
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, this.chatRoom.getChatId(), e);

			return false;
		}

		return true;
	}

	@Override
	public void validate() {
		super.validate();

		if (this.absSender == null)
			throw new NullPointerException("absSender");
		if (this.chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (this.board == null)
			throw new NullPointerException("board");
		if (this.site == null)
			throw new NullPointerException("site");
		if (this.siteHandler == null)
			throw new NullPointerException("siteHandler");
		if (this.fileTransmissionExecutorService == null)
			throw new NullPointerException("fileTransmissionExecutorService");
	}

}
