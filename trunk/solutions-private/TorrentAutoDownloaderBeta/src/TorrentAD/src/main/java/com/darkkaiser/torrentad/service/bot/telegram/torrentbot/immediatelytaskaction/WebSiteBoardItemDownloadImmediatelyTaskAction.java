package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.darkkaiser.torrentad.service.au.transmitter.FileTransmissionExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.util.Tuple;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;
import com.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;
import com.darkkaiser.torrentad.website.WebSiteHandler;
import com.darkkaiser.torrentad.website.WebSiteSearchResultData;

public class WebSiteBoardItemDownloadImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardItemDownloadImmediatelyTaskAction.class);
	
	private final int messageId;

	private final AbsSender absSender;

	private final ChatRoom chatRoom;

	private final long boardItemIdentifier;
	
	private final long boardItemDownloadLinkIndex;

	private final FileTransmissionExecutorService fileTransmissionExecutorService;

	private final WebSite site;

	private final WebSiteHandler siteHandler;

	// 조회한 게시물의 첨부파일을 다운로드시에 사용
	private WebSiteBoard board;

	// 검색한 게시물의 첨부파일을 다운로드시에 검색 결과데이터를 찾기 위한 ID
	private String searchResultDataIdentifier;

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

	public WebSiteBoardItemDownloadImmediatelyTaskAction(int messageId, AbsSender absSender, ChatRoom chatRoom, String searchResultDataIdentifier, long boardItemIdentifier, long boardItemDownloadLinkIndex, 
			TorrentBotResource torrentBotResource, FileTransmissionExecutorService fileTransmissionExecutorService) {

		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (StringUtil.isBlank(searchResultDataIdentifier) == true)
			throw new IllegalArgumentException("searchResultDataIdentifier는 빈 문자열을 허용하지 않습니다.");
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

		this.searchResultDataIdentifier = searchResultDataIdentifier;

		this.boardItemIdentifier = boardItemIdentifier;
		this.boardItemDownloadLinkIndex = boardItemDownloadLinkIndex;
		
		this.site = torrentBotResource.getSite();
		this.siteHandler = (WebSiteHandler) torrentBotResource.getSiteConnector().getConnection();

		this.fileTransmissionExecutorService = fileTransmissionExecutorService;
	}

	@Override
	public String getName() {
		if (this.board != null)		// 조회한 게시물의 첨부파일 다운로드
			return String.format("%s > 조회(%s) > %d > 첨부파일(%d) 다운로드", this.site.getName(), this.board.getDescription(), this.boardItemIdentifier, this.boardItemDownloadLinkIndex);
		else						// 검색한 게시물의 첨부파일 다운로드
			return String.format("%s > 검색(%s) > %d > 첨부파일(%d) 다운로드", this.site.getName(), this.searchResultDataIdentifier, this.boardItemIdentifier, this.boardItemDownloadLinkIndex);
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// 선택된 게시판을 조회한다.
			Iterator<WebSiteBoardItem> iterator = null;
			if (this.board != null) {	// 조회한 게시물의 첨부파일 다운로드
				iterator = this.siteHandler.list(this.board, false, new WebSiteBoardItemComparatorIdentifierDesc());
			} else {					// 검색한 게시물의 첨부파일 다운로드
				assert StringUtil.isBlank(this.searchResultDataIdentifier) == false;
				WebSiteSearchResultData searchResultData = this.siteHandler.getSearchResultData(this.searchResultDataIdentifier);
				if (searchResultData != null)
					iterator = searchResultData.resultIterator(new WebSiteBoardItemComparatorIdentifierDesc());
			}

			if (iterator != null) {
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

						// 인라인 키보드를 설정한다.
						List<InlineKeyboardButton> keyboardButtonList01 = Arrays.asList(
								new InlineKeyboardButton()
										.setText(BotCommandConstants.TSSR_REFRESH_ETC_INLINE_KEYBOARD_BUTTON_TEXT)
										.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.TSSR_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.TSSR_REFRESH_ETC_INLINE_KEYBOARD_BUTTON_DATA))
						);

						InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList01));

						BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "선택한 첨부파일의 다운로드가 완료되었습니다.", this.messageId, inlineKeyboardMarkup);
					}

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

	@Override
	public void validate() {
		super.validate();

		if (this.absSender == null)
			throw new NullPointerException("absSender");
		if (this.chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (this.board == null && StringUtil.isBlank(searchResultDataIdentifier) == true)
			throw new IllegalStateException();
		if (this.site == null)
			throw new NullPointerException("site");
		if (this.siteHandler == null)
			throw new NullPointerException("siteHandler");
		if (this.fileTransmissionExecutorService == null)
			throw new NullPointerException("fileTransmissionExecutorService");
	}

}