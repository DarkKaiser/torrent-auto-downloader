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
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardListImmediatelyTaskAction.class);

	private final long requestId;

	private final int messageId;

	private final AbsSender absSender;

	private final ChatRoom chatRoom;

	private final WebSiteBoard board;

	private final TorrentBotResource torrentBotResource;
	
	private final WebSite site;

	public WebSiteBoardListImmediatelyTaskAction(long requestId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, TorrentBotResource torrentBotResource) {
		this(requestId, BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID, absSender, chatRoom, board, torrentBotResource);
	}

	public WebSiteBoardListImmediatelyTaskAction(long requestId, int messageId, AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, TorrentBotResource torrentBotResource) {
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

		this.board = board;
		this.chatRoom = chatRoom;
		this.requestId = requestId;
		this.messageId = messageId;
		this.absSender = absSender;
		this.torrentBotResource = torrentBotResource;
		this.site = torrentBotResource.getSite();
	}

	@Override
	public String getName() {
		return String.format("%s > %s 조회", this.site.getName(), this.board.getDescription());
	}

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
			Iterator<WebSiteBoardItem> iterator = handler.list(this.board, true, new WebSiteBoardItemComparatorIdentifierDesc());

			// 현재의 작업요청 이후에 클라이언트로부터 새로운 작업요청이 들어온 경우, 현재 작업요청을 클라이언트로 알리지 않는다.
			if (this.chatRoom.getRequestId() != this.requestId)
				return true;

			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("[ ").append(this.board.getDescription()).append(" ] 게시판 조회가 완료되었습니다.\n\n");

			if (iterator.hasNext() == false) {
				sbAnswerMessage.append("조회 결과 데이터가 없습니다.");

				BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());

				return true;
			}

			// 조회된 게시물의 목록을 구한다.
			long identifierMinValue = Long.MAX_VALUE;
			long identifierMaxValue = Long.MIN_VALUE;
			for (int index = 0; iterator.hasNext() == true && index < BotCommandConstants.LASR_OUTPUT_BOARD_ITEM_COUNT; ++index) {
				WebSiteBoardItem boardItem = iterator.next();
				identifierMinValue = Math.min(identifierMinValue, boardItem.getIdentifier());
				identifierMaxValue = Math.max(identifierMaxValue, boardItem.getIdentifier());

				// @@@@@ 추후 문자열 수정
				sbAnswerMessage.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.INLINE_COMMAND_DOWNLOAD_LINK_LIST, boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
			}

			// 인라인 키보드를 설정한다.
			List<InlineKeyboardButton> keyboardButtonList = Arrays.asList(
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA, this.board.getCode())),
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA, this.board.getCode(), Long.toString(identifierMaxValue))),
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA, this.board.getCode(), Long.toString(identifierMinValue)))
			);

			InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList));

			// 클라이언트로 조회된 결과 메시지를 전송한다.
			if (this.messageId == BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID) {
				BotCommandUtils.sendMessage(absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString(), inlineKeyboardMarkup);
			} else {
				BotCommandUtils.editMessageText(absSender, this.chatRoom.getChatId(), messageId, sbAnswerMessage.toString(), inlineKeyboardMarkup);
			}
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
		if (this.board == null)
			throw new NullPointerException("board");
		if (this.torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (this.site == null)
			throw new NullPointerException("site");
	}

}
