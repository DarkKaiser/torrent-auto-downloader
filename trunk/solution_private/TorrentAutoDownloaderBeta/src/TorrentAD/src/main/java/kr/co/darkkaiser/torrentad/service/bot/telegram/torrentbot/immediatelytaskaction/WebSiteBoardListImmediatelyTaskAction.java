package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.website.LoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.NoPermissionException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;
import kr.co.darkkaiser.torrentad.website.WebSiteConstants;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractWebSiteBoardImmediatelyTaskAction {

	public WebSiteBoardListImmediatelyTaskAction(long requestId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, TorrentBotResource torrentBotResource) {
		this(requestId, BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID, absSender, chatRoom, board, torrentBotResource);
	}

	public WebSiteBoardListImmediatelyTaskAction(long requestId, int messageId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, TorrentBotResource torrentBotResource) {
		super(requestId, messageId, absSender, chatRoom, board, torrentBotResource);
	}

	@Override
	public String getName() {
		return String.format("%s > 조회(%s)", this.site.getName(), this.board.getDescription());
	}

	@Override
	protected Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException {
		return this.siteHandler.list(this.board, true, new WebSiteBoardItemComparatorIdentifierDesc());
	}

	@Override
	protected String getExecuteCompletedString() {
		return "게시판 조회가 완료되었습니다";
	}

	@Override
	protected String getExecuteNoResultDataString() {
		return "조회 결과 데이터가 없습니다.";
	}

	@Override
	protected String generateCallbackQueryCommandString(String inlineKeyboardButtonData, long identifierValue) {
		if (identifierValue == WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE)
			return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, inlineKeyboardButtonData, this.board.getCode());

		return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, inlineKeyboardButtonData, this.board.getCode(), Long.toString(identifierValue));
	}

	@Override
	protected String generateDownloadLinkInquiryRequestInlineCommandString(WebSiteBoardItem boardItem) {
		if (boardItem == null)
			throw new NullPointerException("boardItem");

		return BotCommandUtils.toComplexBotCommandString(
				BotCommandConstants.LASR_LIST_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND, boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()));
	}

	@Override
	public void validate() {
		super.validate();
	}

}
