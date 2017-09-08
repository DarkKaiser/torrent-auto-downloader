package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.website.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class AbstractWebSiteBoardImmediatelyTaskAction extends AbstractImmediatelyTaskAction {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSiteBoardImmediatelyTaskAction.class);

	protected final long requestId;

	protected final int messageId;

	protected final AbsSender absSender;

	protected final ChatRoom chatRoom;

	protected final WebSiteBoard board;
	
	protected final WebSite site;

	protected final WebSiteHandler siteHandler;

	public AbstractWebSiteBoardImmediatelyTaskAction(final long requestId, final int messageId, final AbsSender absSender, final ChatRoom chatRoom, final WebSiteBoard board, final TorrentBotResource torrentBotResource) {
		Objects.requireNonNull(absSender, "absSender");
		Objects.requireNonNull(chatRoom, "chatRoom");
		Objects.requireNonNull(board, "board");
		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(torrentBotResource.getSite(), "site");
		Objects.requireNonNull(torrentBotResource.getSiteConnector(), "siteConnector");
		Objects.requireNonNull(torrentBotResource.getSiteConnector().getConnection(), "siteConnection");

		this.requestId = requestId;
		this.messageId = messageId;
		this.absSender = absSender;
		this.chatRoom = chatRoom;

		this.board = board;

		this.site = torrentBotResource.getSite();
		this.siteHandler = (WebSiteHandler) torrentBotResource.getSiteConnector().getConnection();
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// 선택된 게시판을 조회 및 검색한다.
			Iterator<WebSiteBoardItem> iterator = execute();

			// 현재의 작업요청 이후에 클라이언트로부터 새로운 작업요청이 들어온 경우, 현재 작업요청을 클라이언트로 알리지 않는다.
			if (this.chatRoom.getRequestId() != this.requestId)
				return true;

			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("[ ").append(this.board.getDescription()).append(" ] ").append(getExecuteCompletedString()).append(":\n\n");

			if (iterator.hasNext() == false) {
				sbAnswerMessage.append(getExecuteNoResultDataString());

				BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());

				return true;
			}

			// 조회된 게시물의 목록을 구한다.
			long identifierMinValue = Long.MAX_VALUE;
			long identifierMaxValue = Long.MIN_VALUE;
			for (int index = 0; iterator.hasNext() == true && index < BotCommandConstants.LASR_BOARD_ITEM_OUTPUT_COUNT; ++index) {
				WebSiteBoardItem boardItem = iterator.next();
				identifierMinValue = Math.min(identifierMinValue, boardItem.getIdentifier());
				identifierMaxValue = Math.max(identifierMaxValue, boardItem.getIdentifier());

				sbAnswerMessage.append("☞ (").append(boardItem.getRegistDateString()).append(") ").append(boardItem.getTitle()).append("\n").append(generateDownloadLinkInquiryRequestInlineCommandString(boardItem)).append("\n\n");
			}

			// 인라인 키보드를 설정한다.
			//noinspection ArraysAsListWithZeroOrOneArgument
			List<InlineKeyboardButton> keyboardButtonList01 = Arrays.asList(
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(generateCallbackQueryCommandString(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA, WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE))
			);
			List<InlineKeyboardButton> keyboardButtonList02 = Arrays.asList(
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(generateCallbackQueryCommandString(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA, identifierMaxValue)),
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(generateCallbackQueryCommandString(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA, identifierMinValue))
			);

			InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList01, keyboardButtonList02));

			// 클라이언트로 조회 및 검색된 결과 메시지를 전송한다.
			if (this.messageId == BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID) {
				BotCommandUtils.sendMessage(absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString(), inlineKeyboardMarkup);
			} else {
				BotCommandUtils.editMessageText(absSender, this.chatRoom.getChatId(), messageId, sbAnswerMessage.toString(), inlineKeyboardMarkup);
			}
		} catch (final Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, this.chatRoom.getChatId(), e);

			return false;
		}

		return true;
	}

	protected abstract Iterator<WebSiteBoardItem> execute() throws NoPermissionException, LoadBoardItemsException;

	protected abstract String getExecuteCompletedString();

	protected abstract String getExecuteNoResultDataString();

	protected abstract String generateCallbackQueryCommandString(final String inlineKeyboardButtonData, final long identifierValue);

	protected abstract String generateDownloadLinkInquiryRequestInlineCommandString(final WebSiteBoardItem boardItem);

	@Override
	public void validate() {
		super.validate();

		Objects.requireNonNull(this.absSender, "absSender");
		Objects.requireNonNull(this.chatRoom, "chatRoom");
		Objects.requireNonNull(this.board, "board");
		Objects.requireNonNull(this.site, "site");
		Objects.requireNonNull(this.siteHandler, "siteHandler");
	}

}
