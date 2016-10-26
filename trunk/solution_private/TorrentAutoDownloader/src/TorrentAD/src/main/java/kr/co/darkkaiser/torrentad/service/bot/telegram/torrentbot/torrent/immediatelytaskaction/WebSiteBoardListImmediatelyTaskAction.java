package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.immediatelytaskaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.AbstractImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ResourceGet;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardListImmediatelyTaskAction.class);

	private final WebSiteBoard board;
	
	private WebSiteConnector connector;
	
	public AbsSender absSender;
	public User user;
	public Chat chat;

	public WebSiteBoardListImmediatelyTaskAction(ResourceGet get, WebSiteBoard board) {
//		if (connector == null)
//			throw new NullPointerException("connector");
		if (board == null)
			throw new NullPointerException("board");

		this.board = board;
//		this.connector = connector;
	}

	@Override
	public String getName() {
		return String.format("%s > %s 조회", this.connector.getSite().getName(), this.board.getDescription());
	}

	@Override
	public Boolean call() throws Exception {
		try {
			WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();
			Iterator<WebSiteBoardItem> iterator = handler.list(this.board, true);

			// @@@@@ 읽어드린 게시물 데이터를 클라이언트로 전송
			ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
			replyKeyboardMarkup.setSelective(true);
			replyKeyboardMarkup.setResizeKeyboard(true);
			replyKeyboardMarkup.setOneTimeKeyboad(true);
			
			int i = 0;
			List<KeyboardRow> keyboard = new ArrayList<>();
			while (iterator.hasNext() == true) {
				i++;
				if (i == 10)
					break;
//				StringBuilder sbMessage = new StringBuilder();
				WebSiteBoardItem next = iterator.next();
				// System.out.println(next);
				KeyboardRow keyboardFirstRow = new KeyboardRow();
				keyboardFirstRow.add("게시물 " + next.toString());
				
				keyboard.add(keyboardFirstRow);
//				sbMessage.append(next + "\n");
//
//				SendMessage helpMessage = new SendMessage()
//						.setChatId(chat.getId().toString())
//						.setText(sbMessage.toString())
//						.enableHtml(true);
//				
//				try {
//					absSender.sendMessage(helpMessage);
//				} catch (TelegramApiException e) {
//					logger.error(null, e);
//				}
			}
			
			replyKeyboardMarkup.setKeyboard(keyboard);
			
			// 토렌트봇으로 메시지 보내기
			SendMessage helpMessage = new SendMessage().setChatId(chat.getId().toString())
					.enableHtml(true)
					.setText("조회 완료되었습니다.")
					.setReplyMarkup(replyKeyboardMarkup);
			
			try {
				absSender.sendMessage(helpMessage);
			} catch (TelegramApiException e) {
				logger.error(null, e);
			}

		} catch (FailedLoadBoardItemsException e) {
			// @@@@@
//			logger.error("게시판 데이터를 로드하는 중에 예외가 발생하였습니다.", e);
			return false;
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

		if (this.connector == null)
			throw new NullPointerException("connector");
		if (this.board == null)
			throw new NullPointerException("board");
	}

}
