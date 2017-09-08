package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.website.*;
import org.jsoup.helper.StringUtil;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Iterator;

public class WebSiteBoardSearchResultDownloadLinkInquiryImmediatelyTaskAction extends AbstractWebSiteBoardDownloadLinkInquiryImmediatelyTaskAction {
	
	private final String searchResultDataIdentifier;
	
	public WebSiteBoardSearchResultDownloadLinkInquiryImmediatelyTaskAction(final int messageId, final AbsSender absSender, final ChatRoom chatRoom, final String searchResultDataIdentifier, final long boardItemIdentifier, final TorrentBotResource torrentBotResource) {
		super(messageId, absSender, chatRoom, boardItemIdentifier, torrentBotResource);

		if (StringUtil.isBlank(searchResultDataIdentifier) == true)
			throw new IllegalArgumentException("searchResultDataIdentifier는 빈 문자열을 허용하지 않습니다.");

		this.searchResultDataIdentifier = searchResultDataIdentifier;
	}

	@Override
	public String getName() {
		return String.format("%s > 검색(%s) > 첨부파일(%d) 조회", this.site.getName(), this.searchResultDataIdentifier, this.boardItemIdentifier);
	}
	
	@Override
	protected Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException {
		WebSiteSearchResultData searchResultData = this.siteHandler.getSearchResultData(this.searchResultDataIdentifier);
		if (searchResultData != null)
			return searchResultData.resultIterator(new WebSiteBoardItemComparatorIdentifierDesc());

		return null;
	}

	@Override
	protected String generateDownloadRequestInlineCommandString(final WebSiteBoardItem boardItem, final int index) {
		assert this.boardItemIdentifier == boardItem.getIdentifier();
		
		return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.DOWNLOAD_REQUEST_INLINE_COMMAND, this.searchResultDataIdentifier, Long.toString(boardItem.getIdentifier()), Integer.toString(index));
	}

	@Override
	public void validate() {
		super.validate();

		if (StringUtil.isBlank(this.searchResultDataIdentifier) == true)
			throw new NullPointerException("searchResultDataIdentifier");
	}

}
