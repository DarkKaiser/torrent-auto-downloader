package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.util.Tuple;
import com.darkkaiser.torrentad.website.*;
import org.jsoup.internal.StringUtil;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Iterator;
import java.util.Objects;

public class WebSiteBoardSearchImmediatelyTaskAction extends AbstractWebSiteBoardImmediatelyTaskAction {
	
	private final String keyword;
	
	private String searchResultDataIdentifier;

	public WebSiteBoardSearchImmediatelyTaskAction(final long requestId, final AbsSender absSender, final ChatRoom chatRoom, final WebSiteBoard board, final String keyword, final TorrentBotResource torrentBotResource) {
		this(requestId, BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID, absSender, chatRoom, board, keyword, torrentBotResource);
	}

	public WebSiteBoardSearchImmediatelyTaskAction(final long requestId, final int messageId, final AbsSender absSender, final ChatRoom chatRoom, final WebSiteBoard board, final String keyword, final TorrentBotResource torrentBotResource) {
		super(requestId, messageId, absSender, chatRoom, board, torrentBotResource);

		if (StringUtil.isBlank(keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");

		this.keyword = keyword;
	}

	@Override
	public String getName() {
		return String.format("%s > 검색(%s)", this.site.getName(), this.board.getDescription());
	}

	@Override
	protected Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException {
		Tuple<String, Iterator<WebSiteBoardItem>> tuple = this.siteHandler.search(this.board, this.keyword, new WebSiteBoardItemComparatorIdentifierDesc());
		this.searchResultDataIdentifier = tuple.first();
		return tuple.last();
	}

	@Override
	protected String getExecuteCompletedString() {
		return "게시판 검색이 완료되었습니다";
	}

	@Override
	protected String getExecuteNoResultDataString() {
		return "검색 결과 데이터가 없습니다.";
	}

	@Override
	protected String generateCallbackQueryCommandString(final String inlineKeyboardButtonData, final long identifierValue) {
		if (identifierValue == WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE)
			return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND, inlineKeyboardButtonData, this.searchResultDataIdentifier);

		return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND, inlineKeyboardButtonData, this.searchResultDataIdentifier, Long.toString(identifierValue));
	}

	@Override
	protected String generateDownloadLinkInquiryRequestInlineCommandString(final WebSiteBoardItem boardItem) {
		Objects.requireNonNull(boardItem, "boardItem");

		return BotCommandUtils.toComplexBotCommandString(
				BotCommandConstants.LASR_SEARCH_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND, this.searchResultDataIdentifier, Long.toString(boardItem.getIdentifier()));
	}

	@Override
	public void validate() {
		super.validate();
		
		if (StringUtil.isBlank(this.keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");
	}

}
