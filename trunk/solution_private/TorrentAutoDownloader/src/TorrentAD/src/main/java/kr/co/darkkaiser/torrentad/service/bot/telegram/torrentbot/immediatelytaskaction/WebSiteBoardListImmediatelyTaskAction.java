package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.AbstractImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardListImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardListImmediatelyTaskAction.class);

	private final long requestId;

	private final WebSite site;

	private final WebSiteBoard board;

	private final ChatRoom chatRoom;

	private final TorrentBotResource torrentBotResource;
	
	private final AbsSender absSender;

	public WebSiteBoardListImmediatelyTaskAction(long requestId, WebSiteBoard board, ChatRoom chatRoom, TorrentBotResource torrentBotResource, AbsSender absSender) {
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
	}

	@Override
	public String getName() {
		return String.format("%s > %s 조회", this.site.getName(), this.board.getDescription());
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// connector 로그인은 누가 할것인가?

		      SendMessage answer = new SendMessage();
		      answer.setChatId(Long.toString(chatRoom.getChatId()));
		      answer.setText("다운로드 하시려는 파일을 클릭하세요.");
		      
		      ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
		      forceReplyKeyboard.setSelective(true);

		      InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
		      
		      List<InlineKeyboardButton> keyboard = new ArrayList<>();
		      InlineKeyboardButton keyboardFirstRow = new InlineKeyboardButton();
		      keyboardFirstRow.setText("TEXT lsadjfla sdfljasldf lasjdfl asdlfj alsdjf lasfja sldjflas dflasjdf lasdjfljaslf");
		      keyboardFirstRow.setCallbackData("callbackData");
		      keyboard.add(keyboardFirstRow);

		      InlineKeyboardButton keyboardFirstRow2 = new InlineKeyboardButton();
		      keyboardFirstRow2.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
		      keyboardFirstRow2.setCallbackData("keyboardFirstRow2");
		      keyboard.add(keyboardFirstRow2);
		      
		      List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
		      InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
		      keyboardFirstRow3.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
		      keyboardFirstRow3.setCallbackData("keyboardFirstRow3");
		      keyboard2.add(keyboardFirstRow3);
		      
		      List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
		      keyboards.add(keyboard);
		      keyboards.add(keyboard2);

				inline.setKeyboard(keyboards);
		      answer.setReplyMarkup(inline);
		      
		      try {
		          absSender.sendMessage(answer);
		      } catch (TelegramApiException e) {
		    	  
		      }
		      
//		      
//		      
//			this.torrentBotResource.getSiteConnector().login();
//			WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
//			
//			
//			Iterator<WebSiteBoardItem> iterator = handler.list(this.board, true);
//
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
