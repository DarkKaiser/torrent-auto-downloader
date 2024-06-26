package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardSearchImmediatelyTaskAction;
import com.darkkaiser.torrentad.website.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
public class WebSiteBoardSearchResultCallbackQueryRequestHandler extends AbstractBotCommandRequestHandler {

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardSearchResultCallbackQueryRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND);

		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 2, 3) == false)
			return false;

		String callbackQueryCommand = parameters[0];
		//noinspection Duplicates
		if (parameters.length >= 3) {
			if (callbackQueryCommand.equals(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == false
					&& callbackQueryCommand.equals(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == false) {
				return false;
			}

            return StringUtil.isNumeric(parameters[2]) != false;
		} else {
            return callbackQueryCommand.equals(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA) != false;
		}
    }

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		WebSiteHandler siteHandler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();

		try {
			String callbackQueryCommand = parameters[0];
			String callbackQueryId = update.getCallbackQuery().getId();
			Integer callbackQueryMessageId = update.getCallbackQuery().getMessage().getMessageId();
			
			WebSiteSearchResultData searchResultData = siteHandler.getSearchResultData(parameters[1]);
			if (searchResultData == null) {
				BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId);
				BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "이전 검색 정보를 찾을 수 없습니다. 검색을 다시 시도하여 주세요.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.");
				return;
			}

			WebSiteBoard board = searchResultData.getBoard();

			//
			// 새로고침 인라인명령
			//
			if (callbackQueryCommand.equals(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId);
				
				// 게시판 검색중 메시지를 사용자에게 보낸다.
				BotCommandUtils.editMessageText(absSender, chatRoom.getChatId(), callbackQueryMessageId, "[ " + board.getDescription() + " ] 게시판을 검색중입니다.");

				// 게시판 검색을 시작한다.
				this.immediatelyTaskExecutorService.submit(
						new WebSiteBoardSearchImmediatelyTaskAction(chatRoom.incrementAndGetRequestId(), callbackQueryMessageId, absSender, chatRoom, board, searchResultData.getKeyword(), this.torrentBotResource));

				return;
			}

			long identifierMinValue = Long.MAX_VALUE;
			long identifierMaxValue = Long.MIN_VALUE;
			long identifierValue = Long.parseLong(parameters[2]);

			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판 검색이 완료되었습니다:\n\n");

			//
			// 다음페이지 인라인명령
			//
			if (callbackQueryCommand.equals(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == true) {				
				Iterator<WebSiteBoardItem> iterator = searchResultData.resultIterator(new WebSiteBoardItemComparatorIdentifierDesc());

				// 조회된 게시물의 다음페이지 목록을 구한다.
				int outputBoardItemCount = 0;
				while (iterator.hasNext() == true && outputBoardItemCount < BotCommandConstants.LASR_BOARD_ITEM_OUTPUT_COUNT) {
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() >= identifierValue)
						continue;

					++outputBoardItemCount;
					identifierMinValue = Math.min(identifierMinValue, boardItem.getIdentifier());
					identifierMaxValue = Math.max(identifierMaxValue, boardItem.getIdentifier());

					sbAnswerMessage.append("☞ (").append(boardItem.getRegistDateString()).append(") ").append(boardItem.getTitle()).append(" ").append(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND, searchResultData.getIdentifier(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
				}

				// 수신된 CallbackQuery에 대한 응답을 보낸다.
				if (outputBoardItemCount == 0) {
					BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId, "마지막 페이지입니다.");
					return;
				} else {
					BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId);
				}
			//
			// 이전페이지 인라인명령
			//
			} else if (callbackQueryCommand.equals(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				Iterator<WebSiteBoardItem> iterator = searchResultData.resultIterator(new WebSiteBoardItemComparatorIdentifierAsc());

				// 조회된 게시물의 이전페이지 목록을 구한다.
				int outputBoardItemCount = 0;
				int offsetBoardItemInfo = sbAnswerMessage.length();
				StringBuilder sbBoardItemInfo = new StringBuilder();

				while (iterator.hasNext() == true && outputBoardItemCount < BotCommandConstants.LASR_BOARD_ITEM_OUTPUT_COUNT) {
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() <= identifierValue)
						continue;

					++outputBoardItemCount;
					identifierMinValue = Math.min(identifierMinValue, boardItem.getIdentifier());
					identifierMaxValue = Math.max(identifierMaxValue, boardItem.getIdentifier());

					sbBoardItemInfo.append("☞ (").append(boardItem.getRegistDateString()).append(") ").append(boardItem.getTitle()).append(" ").append(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_DOWNLOAD_LINK_INQUIRY_REQUEST_INLINE_COMMAND, searchResultData.getIdentifier(), Long.toString(boardItem.getIdentifier()))).append("\n\n");

					sbAnswerMessage.insert(offsetBoardItemInfo, sbBoardItemInfo);
					sbBoardItemInfo.delete(0, sbBoardItemInfo.length());
				}

				// 수신된 CallbackQuery에 대한 응답을 보낸다.
				if (outputBoardItemCount == 0) {
					BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId, "첫 페이지입니다.");
					return;
				} else {
					BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId);
				}
			} else {
				throw new IllegalArgumentException(String.format("지원하지 않는 인라인 명령(%s)입니다.", callbackQueryCommand));
			}

			// 인라인 키보드를 설정한다.
			final InlineKeyboardButton keyboardButton01 = new InlineKeyboardButton();
			keyboardButton01.setText(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT);
			keyboardButton01.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA, searchResultData.getIdentifier()));
			final List<InlineKeyboardButton> keyboardButtonList01 = List.of(keyboardButton01);

			final InlineKeyboardButton keyboardButton02 = new InlineKeyboardButton();
			keyboardButton02.setText(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT);
			keyboardButton02.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA, searchResultData.getIdentifier(), Long.toString(identifierMaxValue)));
			final InlineKeyboardButton keyboardButton03 = new InlineKeyboardButton();
			keyboardButton03.setText(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT);
			keyboardButton03.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_SEARCH_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA, searchResultData.getIdentifier(), Long.toString(identifierMinValue)));
			final List<InlineKeyboardButton> keyboardButtonList02 = Arrays.asList(keyboardButton02, keyboardButton03);

			final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
			inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonList01, keyboardButtonList02));

			// 클라이언트로 조회된 결과 메시지를 전송한다.
			BotCommandUtils.editMessageText(absSender, chatRoom.getChatId(), callbackQueryMessageId, sbAnswerMessage.toString(), inlineKeyboardMarkup);
		} catch (final Exception e) {
			log.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return WebSiteBoardSearchResultCallbackQueryRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
