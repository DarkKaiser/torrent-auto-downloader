package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.website.NoPermissionException;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.website.LoadBoardItemsException;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;
import com.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;

public class WebSiteBoardListResultDownloadLinkInquiryImmediatelyTaskAction extends AbstractWebSiteBoardDownloadLinkInquiryImmediatelyTaskAction {
	
	private final WebSiteBoard board;
	
	public WebSiteBoardListResultDownloadLinkInquiryImmediatelyTaskAction(int messageId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, long boardItemIdentifier, TorrentBotResource torrentBotResource) {
		super(messageId, absSender, chatRoom, boardItemIdentifier, torrentBotResource);

		if (board == null)
			throw new NullPointerException("board");

		this.board = board;
	}

	@Override
	public String getName() {
		return String.format("%s > 조회(%s) > 첨부파일(%d) 조회", this.site.getName(), this.board.getDescription(), this.boardItemIdentifier);
	}
	
	@Override
	protected Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException {
		return this.siteHandler.list(this.board, false, new WebSiteBoardItemComparatorIdentifierDesc());
	}
	
	@Override
	protected String generateDownloadRequestInlineCommandString(WebSiteBoardItem boardItem, int index) {
		assert this.boardItemIdentifier == boardItem.getIdentifier();
		assert this.board.getCode().equals(boardItem.getBoard().getCode()) == true;

		return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.DOWNLOAD_REQUEST_INLINE_COMMAND, boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()), Integer.toString(index));
	}

	@Override
	public void validate() {
		super.validate();

		if (this.board == null)
			throw new NullPointerException("board");
	}

}
