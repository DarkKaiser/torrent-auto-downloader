package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.AbstractBotCommandRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.WebSiteBoardListImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierAscCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;

// @@@@@
public class WebSiteBoardListRequestHandler2 extends AbstractBotCommandRequestHandler {

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardListRequestHandler2(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("lsa", "뺄것");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 2, 3) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar, Update update) {

		//this.torrentBotResource.getSiteConnector().login();
		WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
		
		WebSiteBoard board = BogoBogoBoard.MOVIE_NEW;
		try {

			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판 조회가 완료되었습니다.\n");
			
			sbAnswerMessage.append("\n");
			
			long min = Long.MAX_VALUE;
			long max = Long.MIN_VALUE;
			if (parameters[1].equals("NEXT") == true) {
				Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemIdentifierDescCompare());
				long key = Long.parseLong(parameters[2]);
				
				int i = 0;
				while (iterator.hasNext() == true) {
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() >= key) {
						continue;
					}
					
					++i;
					if (i > 5)
						break;

					if (boardItem.getIdentifier() < min)
						min = boardItem.getIdentifier();
					if (boardItem.getIdentifier() > max)
						max = boardItem.getIdentifier();
					
					sbAnswerMessage.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString("ls", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
				}
				
					AnswerCallbackQuery a1 = new AnswerCallbackQuery();
					a1.setCallbackQueryId(update.getCallbackQuery().getId());
					if (i == 0) {
						a1.setText("다음 데이터가 없습니다.");
						a1.setShowAlert(false);
					}
					try {
						absSender.answerCallbackQuery(a1);
					} catch (TelegramApiException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if (i == 0)
						return;
				
			} else if (parameters[1].equals("PREV") == true) {
				Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemIdentifierAscCompare());
				long key = Long.parseLong(parameters[2]);
				
				StringBuilder temp = new StringBuilder();
				StringBuilder temp2 = new StringBuilder();
				
				int i = 0;
				while (iterator.hasNext() == true) {
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() <= key) {
						continue;
					}

					++i;
					if (i > 5)
						break;

					if (boardItem.getIdentifier() < min)
						min = boardItem.getIdentifier();
					if (boardItem.getIdentifier() > max)
						max = boardItem.getIdentifier();

					temp.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString("ls", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
					temp2.insert(0, temp.toString());
					temp.delete(0, temp.length());
				}
				
					AnswerCallbackQuery a1 = new AnswerCallbackQuery();
					a1.setCallbackQueryId(update.getCallbackQuery().getId());
					if (i == 0) {
						a1.setText("이전 데이터가 없습니다.");
						a1.setShowAlert(false);
					}
					try {
						absSender.answerCallbackQuery(a1);
					} catch (TelegramApiException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (i == 0)
						return;

				sbAnswerMessage.append(temp2.toString());
			} else if (parameters[1].equals("REFRESH") == true) {
				AnswerCallbackQuery a1 = new AnswerCallbackQuery();
				a1.setCallbackQueryId(update.getCallbackQuery().getId());
				try {
					absSender.answerCallbackQuery(a1);
				} catch (TelegramApiException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//@@@@@
				// 게시판 조회중 메시지를 사용자에게 보낸다.
				sbAnswerMessage.delete(0, sbAnswerMessage.length());
				sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판을 조회중입니다. 잠시만 기다려 주세요.");

				EditMessageText ee = new EditMessageText();
				ee.setChatId(Long.toString(chatRoom.getChatId()))
				.setMessageId(update.getCallbackQuery().getMessage().getMessageId())
				.setText(sbAnswerMessage.toString())
				.enableHtml(true);

				try {
					absSender.editMessageText(ee);
				} catch (TelegramApiException e) {
//					logger.error(null, e);
				}

				//sendAnswerMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());

				// 게시판 조회를 시작한다.
				this.immediatelyTaskExecutorService.submit(
						new WebSiteBoardListImmediatelyTaskAction(chatRoom.incrementAndGetRequestId(), board, chatRoom, this.torrentBotResource, absSender, update.getCallbackQuery().getMessage().getMessageId()));

				return;
			}

			EditMessageText answerMessage = new EditMessageText()
					.setChatId(Long.toString(chatRoom.getChatId()))
					.setMessageId(update.getCallbackQuery().getMessage().getMessageId())
					.setText(sbAnswerMessage.toString())
					.enableHtml(true);

			InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
			List<InlineKeyboardButton> keyboard = new ArrayList<>();
			InlineKeyboardButton inlineKeyboardPreviousButton = new InlineKeyboardButton();
			inlineKeyboardPreviousButton.setText("이전");
			inlineKeyboardPreviousButton.setCallbackData(String.format("lsa_%s_%s_%s", board.getCode(), "PREV", Long.toString(max)));
			keyboard.add(inlineKeyboardPreviousButton);

			InlineKeyboardButton inlineKeyboardNextButton = new InlineKeyboardButton();
			inlineKeyboardNextButton.setText("다음");
			inlineKeyboardNextButton.setCallbackData(String.format("lsa_%s_%s_%s", board.getCode(), "NEXT", Long.toString(min)));
			keyboard.add(inlineKeyboardNextButton);

//			List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
			InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
			keyboardFirstRow3.setText("새로읽기");
			keyboardFirstRow3.setCallbackData(String.format("lsa_%s_%s", board.getCode(), "REFRESH"));
			keyboard.add(keyboardFirstRow3);

			List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
			keyboards.add(keyboard);
//			keyboards.add(keyboard2);

			inlineKeyboardMarkup.setKeyboard(keyboards);
			answerMessage.setReplyMarkup(inlineKeyboardMarkup);

			try {
				absSender.editMessageText(answerMessage);
			} catch (TelegramApiException e) {
//				logger.error(null, e);
			}
		} catch (FailedLoadBoardItemsException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardListRequestHandler2.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
