package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.website.NoPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.website.LoadBoardItemsException;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;
import com.darkkaiser.torrentad.website.WebSiteBoardItemDownloadLink;
import com.darkkaiser.torrentad.website.WebSiteHandler;

public abstract class AbstractWebSiteBoardDownloadLinkInquiryImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSiteBoardDownloadLinkInquiryImmediatelyTaskAction.class);
	
	protected final int messageId;

	protected final AbsSender absSender;

	protected final ChatRoom chatRoom;
	
	protected final long boardItemIdentifier;

	protected final WebSite site;

	protected final WebSiteHandler siteHandler;

	public AbstractWebSiteBoardDownloadLinkInquiryImmediatelyTaskAction(int messageId, AbsSender absSender, ChatRoom chatRoom, long boardItemIdentifier, TorrentBotResource torrentBotResource) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (torrentBotResource.getSiteConnector() == null)
			throw new NullPointerException("siteConnector");
		if (torrentBotResource.getSiteConnector().getConnection() == null)
			throw new NullPointerException("siteConnection");

		this.messageId = messageId;
		this.boardItemIdentifier = boardItemIdentifier;

		this.chatRoom = chatRoom;
		this.absSender = absSender;

		this.site = torrentBotResource.getSite();
		this.siteHandler = (WebSiteHandler) torrentBotResource.getSiteConnector().getConnection();
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// 선택된 게시판을 검색 또는 조회한다.
			Iterator<WebSiteBoardItem> iterator = execute();
			if (iterator != null) {
				while (iterator.hasNext() == true) {
					// 사용자가 선택한 게시물을 찾는다.
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() != this.boardItemIdentifier)
						continue;
	
					// 해당 게시물의 첨부파일에 대한 다운로드 링크가 없는경우 읽어들인다.
					Iterator<WebSiteBoardItemDownloadLink> downloadLinkIterator = boardItem.downloadLinkIterator();
					if (downloadLinkIterator.hasNext() == false) {
						if (this.siteHandler.loadDownloadLink(boardItem) == false) {
							BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "선택된 게시물의 첨부파일 확인이 실패하였습니다.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.", this.messageId);
							return true;
						}
					}
	
					downloadLinkIterator = boardItem.downloadLinkIterator();
					if (downloadLinkIterator.hasNext() == false) {
						BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "선택된 게시물의 첨부파일이 존재하지 않습니다.", this.messageId);
						return true;
					}
	
					// 선택된 게시물의 첨부파일에 대한 정보를 사용자에게 보낸다.
					StringBuilder sbAnswerMessage = new StringBuilder();
					sbAnswerMessage.append("선택된 게시물의 첨부파일 확인이 완료되었습니다:\n\n");
	
					for (int index = 0; downloadLinkIterator.hasNext() == true; ++index) {
						WebSiteBoardItemDownloadLink next = downloadLinkIterator.next();
						sbAnswerMessage.append("☞ ").append(next).append("\n").append(generateDownloadRequestInlineCommandString(boardItem, index)).append("\n\n");
					}
	
					BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString(), this.messageId);
	
					return true;
				}
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
	
	protected abstract Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException;
	
	protected abstract String generateDownloadRequestInlineCommandString(WebSiteBoardItem boardItem, int index);

	@Override
	public void validate() {
		super.validate();

		if (this.absSender == null)
			throw new NullPointerException("absSender");
		if (this.chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (this.site == null)
			throw new NullPointerException("site");
		if (this.siteHandler == null)
			throw new NullPointerException("siteHandler");
	}

}
