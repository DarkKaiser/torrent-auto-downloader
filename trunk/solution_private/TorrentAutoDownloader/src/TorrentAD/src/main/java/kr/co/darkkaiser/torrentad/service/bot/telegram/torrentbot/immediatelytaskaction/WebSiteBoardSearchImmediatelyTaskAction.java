package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.jsoup.helper.StringUtil;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.website.LoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.NoPermissionException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;

public class WebSiteBoardSearchImmediatelyTaskAction extends AbstractWebSiteBoardImmediatelyTaskAction {
	
	private final String keyword;

	public WebSiteBoardSearchImmediatelyTaskAction(long requestId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, String keyword, TorrentBotResource torrentBotResource) {
		this(requestId, BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID, absSender, chatRoom, board, keyword, torrentBotResource);
	}

	public WebSiteBoardSearchImmediatelyTaskAction(long requestId, int messageId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, String keyword, TorrentBotResource torrentBotResource) {
		super(requestId, messageId, absSender, chatRoom, board, torrentBotResource);

		if (StringUtil.isBlank(keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");

		this.keyword = keyword;
	}

	@Override
	public String getName() {
		return String.format("%s > %s 검색", this.site.getName(), this.board.getDescription());
	}

	@Override
	protected Iterator<WebSiteBoardItem> lasIterator() throws NoPermissionException, LoadBoardItemsException {
		return this.siteHandler.search(this.board, this.keyword, true, new WebSiteBoardItemComparatorIdentifierDesc());
	}

	@Override
	protected String lasCompletedString() {
		return "게시판 검색이 완료되었습니다";
	}

	@Override
	protected String lasNoResultDataString() {
		return "검색 결과 데이터가 없습니다.";
	}

	@Override
	protected String lasCallbackQueryCommand() {
		return BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND;
	}

	@Override
	protected String lasDownloadLinkListInlineCommand() {
		return BotCommandConstants.INLINE_COMMAND_LASR_SEARCH_RESULT_DOWNLOAD_LINK_LIST;
	}

	@Override
	public void validate() {
		super.validate();
		
		if (StringUtil.isBlank(this.keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");
	}

}
