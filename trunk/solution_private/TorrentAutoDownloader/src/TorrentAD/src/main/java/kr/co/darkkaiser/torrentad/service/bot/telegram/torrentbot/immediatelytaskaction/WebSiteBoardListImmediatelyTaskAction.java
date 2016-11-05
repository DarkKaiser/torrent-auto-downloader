package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractTorrentBotImmediatelyTaskAction {
	
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
			// connector 로그인은 누가 할것인가?
			this.torrentBotResource.getSiteConnector().login();
			WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
			
			////////////////////////////////////////////////////////////////

			// 게시판을 조회한다.
			Iterator<WebSiteBoardItem> iterator = handler.list(this.board, true, new WebSiteBoardItemIdentifierDescCompare());
			
			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("[ ").append(this.board.getDescription()).append(" ] 게시판 조회가 완료되었습니다.\n");
			
			if (iterator.hasNext() == false) {
				// @@@@@
				sbAnswerMessage.append("\n조회된 게시물이 없습니다.");

				sendAnswerMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());
			} else {
				sbAnswerMessage.append("\n");
				
				// @@@@@
				int i = 0;
				long min = Long.MAX_VALUE;
				long max = Long.MIN_VALUE;
				while (iterator.hasNext() == true) {
					++i;
					if (i > 5)
						break;
					
					WebSiteBoardItem boardItem = iterator.next();
					if (boardItem.getIdentifier() < min)
						min = boardItem.getIdentifier();
					if (boardItem.getIdentifier() > max)
						max = boardItem.getIdentifier();
					
					sbAnswerMessage.append(boardItem.getIdentifier()).append(" : ").append(boardItem.getRegistDateString()).append(" : ").append(boardItem.getTitle().trim()).append(" ").append(BotCommandUtils.toComplexBotCommandString("ls", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()))).append("\n\n");
				}

				if (this.messageId == -1) {
					SendMessage answerMessage = new SendMessage()
							.setChatId(Long.toString(this.chatRoom.getChatId()))
//							.setMessageId(this.messageId)
							.setText(sbAnswerMessage.toString())
							.enableHtml(true);

					InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
					List<InlineKeyboardButton> keyboard = new ArrayList<>();
					InlineKeyboardButton inlineKeyboardPreviousButton = new InlineKeyboardButton();
					inlineKeyboardPreviousButton.setText("이전");
					inlineKeyboardPreviousButton.setCallbackData(String.format("lsa_%s_%s_%s", this.board.getCode(), "PREV", Long.toString(max)));
					keyboard.add(inlineKeyboardPreviousButton);

					InlineKeyboardButton inlineKeyboardNextButton = new InlineKeyboardButton();
					inlineKeyboardNextButton.setText("다음");
					inlineKeyboardNextButton.setCallbackData(String.format("lsa_%s_%s_%s", this.board.getCode(), "NEXT", Long.toString(min)));
					keyboard.add(inlineKeyboardNextButton);

//					List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
					InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
					keyboardFirstRow3.setText("새로읽기");
					keyboardFirstRow3.setCallbackData(String.format("lsa_%s_%s", this.board.getCode(), "REFRESH"));
					keyboard.add(keyboardFirstRow3);

					List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
					keyboards.add(keyboard);
//					keyboards.add(keyboard2);

					inlineKeyboardMarkup.setKeyboard(keyboards);
					answerMessage.setReplyMarkup(inlineKeyboardMarkup);

//					sendAnswerMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());

					try {
						absSender.sendMessage(answerMessage);
					} catch (TelegramApiException e) {
						logger.error(null, e);
					}
				} else {
					EditMessageText answerMessage = new EditMessageText()
							.setChatId(Long.toString(this.chatRoom.getChatId()))
							.setMessageId(this.messageId)
							.setText(sbAnswerMessage.toString())
							.enableHtml(true);

					InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
					List<InlineKeyboardButton> keyboard = new ArrayList<>();
					InlineKeyboardButton inlineKeyboardPreviousButton = new InlineKeyboardButton();
					inlineKeyboardPreviousButton.setText("이전");
					inlineKeyboardPreviousButton.setCallbackData(String.format("lsa_%s_%s_%s", this.board.getCode(), "PREV", Long.toString(max)));
					keyboard.add(inlineKeyboardPreviousButton);

					InlineKeyboardButton inlineKeyboardNextButton = new InlineKeyboardButton();
					inlineKeyboardNextButton.setText("다음");
					inlineKeyboardNextButton.setCallbackData(String.format("lsa_%s_%s_%s", this.board.getCode(), "NEXT", Long.toString(min)));
					keyboard.add(inlineKeyboardNextButton);

//					List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
					InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
					keyboardFirstRow3.setText("새로읽기");
					keyboardFirstRow3.setCallbackData(String.format("lsa_%s_%s", this.board.getCode(), "REFRESH"));
					keyboard.add(keyboardFirstRow3);

					List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
					keyboards.add(keyboard);
//					keyboards.add(keyboard2);

					inlineKeyboardMarkup.setKeyboard(keyboards);
					answerMessage.setReplyMarkup(inlineKeyboardMarkup);

//					sendAnswerMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());

					try {
						absSender.editMessageText(answerMessage);
					} catch (TelegramApiException e) {
						logger.error(null, e);
					}
				}
				
			}

//			// @@@@@ 읽어드린 게시물 데이터를 클라이언트로 전송
//			ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//			replyKeyboardMarkup.setSelective(true);
//			replyKeyboardMarkup.setResizeKeyboard(true);
//			replyKeyboardMarkup.setOneTimeKeyboad(true);
//			
//			int i = 0;
//			List<KeyboardRow> keyboard = new ArrayList<>();
//			while (iterator.hasNext() == true) {
//				i++;
//				if (i == 10)
//					break;
////				StringBuilder sbMessage = new StringBuilder();
//				WebSiteBoardItem next = iterator.next();
//				// System.out.println(next);
//				KeyboardRow keyboardFirstRow = new KeyboardRow();
//				keyboardFirstRow.add("게시물 " + next.toString());
//				
//				keyboard.add(keyboardFirstRow);
////				sbMessage.append(next + "\n");
////
////				SendMessage helpMessage = new SendMessage()
////						.setChatId(chat.getId().toString())
////						.setText(sbMessage.toString())
////						.enableHtml(true);
////				
////				try {
////					absSender.sendMessage(helpMessage);
////				} catch (TelegramApiException e) {
////					logger.error(null, e);
////				}
//			}
//			
//			replyKeyboardMarkup.setKeyboard(keyboard);
//			
//			// 토렌트봇으로 메시지 보내기
//			SendMessage helpMessage = new SendMessage().setChatId(Long.toString(this.chatRoom.getChatId()))
//					.enableHtml(true)
//					.setText("조회 완료되었습니다.")
//					.setReplyMarkup(replyKeyboardMarkup);
//			
//			try {
//				absSender.sendMessage(helpMessage);
//			} catch (TelegramApiException e) {
//				logger.error(null, e);
//			}
//		} catch (FailedLoadBoardItemsException e) {
//			// @@@@@
////			logger.error("게시판 데이터를 로드하는 중에 예외가 발생하였습니다.", e);
//			return false;
		} catch (Exception e) {
			logger.error(null, e);
			
			// @@@@@ 클라이언트로 조회실패 전송
			
			return false;
		}

		return true;
	}

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
