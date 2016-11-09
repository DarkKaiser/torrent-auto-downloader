package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.AbstractRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardListImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierAscCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class WebSiteBoardListResultInlineCommandRequestHandler extends AbstractRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardListResultInlineCommandRequestHandler.class);

	private final WebSite site;
	
	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardListResultInlineCommandRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND);

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.site = torrentBotResource.getSite();
		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 2, 3) == false)
			return false;

		String inlineCommand = parameters[0];
		if (parameters.length >= 3) {
			if (inlineCommand.equals(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == false
					&& inlineCommand.equals(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == false) {
				return false;
			}
			
			try {
		        Long.parseLong(parameters[2]);
		    } catch (NumberFormatException e) {
		        return false;
		    }
		} else {
			if (inlineCommand.equals(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA) == false)
				return false;
		}

		if (this.site.getBoardByCode(parameters[1]) == null)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		StringBuilder sbAnswerMessage = new StringBuilder();

		////////////////////////////////////////////////////////////
		// @@@@@
		String inlineCommand = parameters[0];
		WebSiteBoard board = this.site.getBoardByCode(parameters[1]);

		try {
			String callbackQueryId = update.getCallbackQuery().getId();
			Integer callbackQueryMessageId = update.getCallbackQuery().getMessage().getMessageId();

			if (inlineCommand.equals(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId, "");
				
				//@@@@@
				// 게시판 조회중 메시지를 사용자에게 보낸다.
				sbAnswerMessage.delete(0, sbAnswerMessage.length());
				sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판을 조회중입니다. 잠시만 기다려 주세요.");

				BotCommandUtils.editMessageText(absSender, chatRoom.getChatId(), callbackQueryMessageId, sbAnswerMessage.toString());

				// 게시판 조회를 시작한다.
				this.immediatelyTaskExecutorService.submit(
						new WebSiteBoardListImmediatelyTaskAction(chatRoom.incrementAndGetRequestId(), callbackQueryMessageId, absSender, chatRoom, board, this.torrentBotResource));

				return;
			}
			////////////////////////////////////////////////////////////
			
			////////////////////////////////////////////////////////////
			// @@@@@
			WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
			////////////////////////////////////////////////////////////

			sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판 조회가 완료되었습니다.\n\n");
			
			long keyValue = Long.parseLong(parameters[2]);
			long keyMinValue = Long.MAX_VALUE;
			long keyMaxValue = Long.MIN_VALUE;
			if (inlineCommand.equals(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				// 선택된 게시판을 조회한다.
				Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemIdentifierDescCompare());

				int index = 0;
				while (iterator.hasNext() == true) {
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() >= keyValue)
						continue;

					++index;
					if (index > BotCommandConstants.LASR_BOARD_ITEM_OUTPUT_COUNT)
						break;

					keyMinValue = Math.min(keyMinValue, boardItem.getIdentifier());
					keyMaxValue = Math.max(keyMaxValue, boardItem.getIdentifier());

					// @@@@@
					sbAnswerMessage.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString("ls", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
				}
				
				BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId, index == 0 ? "마지막 페이지입니다." : "");
			
				if (index == 0)
					return;
			} else if (inlineCommand.equals(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				// 선택된 게시판을 조회한다.
				Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemIdentifierAscCompare());
				
				StringBuilder temp = new StringBuilder();
				StringBuilder temp2 = new StringBuilder();
				
				int i = 0;
				while (iterator.hasNext() == true) {
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() <= keyValue) {
						continue;
					}

					++i;
					if (i > 5)
						break;

					if (boardItem.getIdentifier() < keyMinValue)
						keyMinValue = boardItem.getIdentifier();
					if (boardItem.getIdentifier() > keyMaxValue)
						keyMaxValue = boardItem.getIdentifier();

					temp.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString("ls", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
					temp2.insert(0, temp.toString());
					temp.delete(0, temp.length());
				}
			
				BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId, i == 0 ? "첫 페이지입니다." : "");

				if (i == 0)
					return;

				sbAnswerMessage.append(temp2.toString());
			}
			////////////////////////////////////////////////////////////

			// 인라인 키보드를 설정한다.
			List<InlineKeyboardButton> keyboardButtonList = Arrays.asList(
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND, BotCommandConstants.LASR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA, board.getCode())),
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND, BotCommandConstants.LASR_PREV_PAGE_INLINE_KEYBOARD_BUTTON_DATA, board.getCode(), Long.toString(keyMaxValue))),
					new InlineKeyboardButton()
							.setText(BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_TEXT)
							.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.LASR_LIST_RESULT_INLINE_COMMAND, BotCommandConstants.LASR_NEXT_PAGE_INLINE_KEYBOARD_BUTTON_DATA, board.getCode(), Long.toString(keyMinValue)))
			);

			InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList));

			// 클라이언트로 조회된 결과 메시지를 전송한다.
			BotCommandUtils.editMessageText(absSender, chatRoom.getChatId(), callbackQueryMessageId, sbAnswerMessage.toString(), inlineKeyboardMarkup);
		} catch (FailedLoadBoardItemsException e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardListResultInlineCommandRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
