package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.website.*;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Iterator;
import java.util.Objects;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractWebSiteBoardImmediatelyTaskAction {

	public WebSiteBoardListImmediatelyTaskAction(final long requestId, final AbsSender absSender, final ChatRoom chatRoom, final WebSiteBoard board, final TorrentBotResource torrentBotResource) {
		this(requestId, BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID, absSender, chatRoom, board, torrentBotResource);
	}

	public WebSiteBoardListImmediatelyTaskAction(final long requestId, final int messageId, final AbsSender absSender, final ChatRoom chatRoom, final WebSiteBoard board, final TorrentBotResource torrentBotResource) {
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
	protected String generateCallbackQueryCommandString(final String inlineKeyboardButtonData, final long identifierValue) {
		if (identifierValue == WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE)
			return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, inlineKeyboardButtonData, this.board.getCode());

		return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, inlineKeyboardButtonData, this.board.getCode(), Long.toString(identifierValue));
	}

	@Override
	protected String generateDownloadLinkInquiryRequestInlineCommandString(final WebSiteBoardItem boardItem) {
		Objects.requireNonNull(boardItem, "boardItem");

		return BotCommandUtils.toComplexBotCommandString(
				BotCommandConstants.LASR_LIST_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND, boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()));
	}

	@Override
	public void validate() {
		super.validate();
	}

}
