package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardListImmediatelyTaskAction.class);

	private final long requestId;

	private final WebSite site;

	private final WebSiteBoard board;

	private final ChatRoom chatRoom;

	private final TorrentBotResource torrentBotResource;
	
	private final AbsSender absSender;
	
	// @@@@@
	private int messageId;

	public WebSiteBoardListImmediatelyTaskAction(long requestId, WebSiteBoard board, ChatRoom chatRoom, TorrentBotResource torrentBotResource, AbsSender absSender, int messageId) {
		if (board == null)
			throw new NullPointerException("board");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");

		this.board = board;
		this.chatRoom = chatRoom;
		this.requestId = requestId;
		this.absSender = absSender;
		this.torrentBotResource = torrentBotResource;
		this.site = torrentBotResource.getSite();
		this.messageId = messageId;
	}

	@Override
	public String getName() {
		return String.format("%s > %s 조회", this.site.getName(), this.board.getDescription());
	}

	// @@@@@
	@Override
	public Boolean call() throws Exception {
		try {
			////////////////////////////////////////////////////////////////
			// @@@@@
			// connector 로그인은 누가 할것인가?
			this.torrentBotResource.getSiteConnector().login();
			WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
			////////////////////////////////////////////////////////////////

			// 선택된 게시판을 조회한다.
			Iterator<WebSiteBoardItem> iterator = handler.list(this.board, true, new WebSiteBoardItemIdentifierDescCompare());
			
			// @@@@@
			if (this.chatRoom.getRequestId() != this.requestId)
				return true;

			if (iterator.hasNext() == false) {
				StringBuilder sbAnswerMessage = new StringBuilder();
				sbAnswerMessage.append("[ ").append(this.board.getDescription()).append(" ] 게시판 조회 결과가 0 건입니다.");

				BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());
			} else {
				StringBuilder sbAnswerMessage = new StringBuilder();
				sbAnswerMessage.append("[ ").append(this.board.getDescription()).append(" ] 게시판 조회가 완료되었습니다.\n\n");

				// 조회된 게시물의 목록을 구한다.
				long keyMinValue = Long.MAX_VALUE;
				long keyMaxValue = Long.MIN_VALUE;
				for (int index = 0; iterator.hasNext() == true && index < BotCommandConstants.LASR_BOARD_ITEM_OUTPUT_COUNT; ++index) {
					WebSiteBoardItem boardItem = iterator.next();
					keyMinValue = Math.min(keyMinValue, boardItem.getIdentifier());
					keyMaxValue = Math.max(keyMaxValue, boardItem.getIdentifier());

					// @@@@@
					sbAnswerMessage.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString("ls", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
				}

				// 인라인 키보드를 설정한다.
				List<InlineKeyboardButton> keyboardButtonList = Arrays.asList(
						new InlineKeyboardButton()
								.setText(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT)
								.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND, BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA, this.board.getCode())),
						new InlineKeyboardButton()
								.setText(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
								.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND, BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA, this.board.getCode(), Long.toString(keyMaxValue))),
						new InlineKeyboardButton()
								.setText(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
								.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND, BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA, this.board.getCode(), Long.toString(keyMinValue)))
				);

				InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList));

				// 클라이언트로 조회된 결과 메시지를 전송한다.
				if (this.messageId == -1) {//@@@@@
					BotCommandUtils.sendMessage(absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString(), inlineKeyboardMarkup);
				} else {
					BotCommandUtils.editMessageText(absSender, this.chatRoom.getChatId(), messageId, sbAnswerMessage.toString(), inlineKeyboardMarkup);
				}
			}
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, this.chatRoom.getChatId(), e);
			
			return false;
		}

		return true;
	}

	//@@@@@
	@Override
	public void validate() {
		super.validate();

		if (this.site == null)
			throw new NullPointerException("site");
		if (this.board == null)
			throw new NullPointerException("board");
		if (this.chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (this.torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (this.absSender == null)
			throw new NullPointerException("absSender");
	}

}
