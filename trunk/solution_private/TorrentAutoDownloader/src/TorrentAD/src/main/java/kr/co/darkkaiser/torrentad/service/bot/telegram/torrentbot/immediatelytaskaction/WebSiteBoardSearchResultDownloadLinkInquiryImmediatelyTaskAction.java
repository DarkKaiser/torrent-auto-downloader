package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.jsoup.helper.StringUtil;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.website.LoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.NoPermissionException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchHistoryData;

public class WebSiteBoardSearchResultDownloadLinkInquiryImmediatelyTaskAction extends AbstractWebSiteBoardDownloadLinkInquiryImmediatelyTaskAction {
	
	private final String historyDataIdentifier;
	
	public WebSiteBoardSearchResultDownloadLinkInquiryImmediatelyTaskAction(int messageId, AbsSender absSender, ChatRoom chatRoom, String historyDataIdentifier, long boardItemIdentifier, TorrentBotResource torrentBotResource) {
		super(messageId, absSender, chatRoom, boardItemIdentifier, torrentBotResource);

		if (StringUtil.isBlank(historyDataIdentifier) == true)
			throw new IllegalArgumentException("historyDataIdentifier는 빈 문자열을 허용하지 않습니다.");

		this.historyDataIdentifier = historyDataIdentifier;
	}

	@Override
	public String getName() {
		return String.format("%s > 검색(%s) > 첨부파일(%d) 조회", this.site.getName(), this.historyDataIdentifier, this.boardItemIdentifier);
	}
	
	@Override
	protected Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException {
		WebSiteSearchHistoryData historyData = this.siteHandler.getSearchHistoryData(this.historyDataIdentifier);
		if (historyData != null)
			return historyData.resultIterator(new WebSiteBoardItemComparatorIdentifierDesc());

		return null;
	}

	@Override
	protected String generateDownloadRequestInlineCommandString(WebSiteBoardItem boardItem, int index) {
		assert this.boardItemIdentifier == boardItem.getIdentifier();
		
		return BotCommandUtils.toComplexBotCommandString(BotCommandConstants.DOWNLOAD_REQUEST_INLINE_COMMAND, this.historyDataIdentifier, Long.toString(boardItem.getIdentifier()), Integer.toString(index));
	}

	@Override
	public void validate() {
		super.validate();

		if (StringUtil.isBlank(this.historyDataIdentifier) == true)
			throw new NullPointerException("historyDataIdentifier");
	}

}
