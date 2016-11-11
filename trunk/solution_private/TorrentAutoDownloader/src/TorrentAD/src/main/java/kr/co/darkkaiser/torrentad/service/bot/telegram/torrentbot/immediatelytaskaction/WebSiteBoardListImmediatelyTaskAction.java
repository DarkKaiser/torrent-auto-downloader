package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.website.LoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.NoPermissionException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractWebSiteBoardImmediatelyTaskAction {
	
	public WebSiteBoardListImmediatelyTaskAction(long requestId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, TorrentBotResource torrentBotResource) {
		super(requestId, BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID, absSender, chatRoom, board, torrentBotResource);
	}

	public WebSiteBoardListImmediatelyTaskAction(long requestId, int messageId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, TorrentBotResource torrentBotResource) {
		super(requestId, messageId, absSender, chatRoom, board, torrentBotResource);
	}

	@Override
	public String getName() {
		return String.format("%s > %s 조회", this.site.getName(), this.board.getDescription());
	}

	protected Iterator<WebSiteBoardItem> lasIterator() throws NoPermissionException, LoadBoardItemsException {
		return this.siteHandler.list(this.board, true, new WebSiteBoardItemComparatorIdentifierDesc());
	}

	@Override
	protected String getLASCompletedString() {
		return "게시판 조회가 완료되었습니다";
	}

	@Override
	protected String getLASNoResultDataString() {
		return "조회 결과 데이터가 없습니다.";
	}

	@Override
	protected String getLASCallbackQueryCommand() {
		return BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND;
	}

	@Override
	protected String getLASDownloadLinkListInlineCommand() {
		return BotCommandConstants.INLINE_COMMAND_LASR_LIST_RESULT_DOWNLOAD_LINK_LIST;
	}

	@Override
	public void validate() {
		super.validate();
	}

}
