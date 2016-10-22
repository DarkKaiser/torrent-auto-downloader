package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.AbstractRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;

//@@@@@
public class SelectedBoardItemRequestHandler extends AbstractRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(SelectedBoardItemRequestHandler.class);

	private final RequestHandlerRegistry requestResponseRegistry;
	
	private final TorrentJob job;
	
	private final ChatRoom chat;
	
	public SelectedBoardItemRequestHandler(RequestHandlerRegistry requestResponseRegistry, TorrentJob job, ChatRoom chat) {
		super("게시물");
		
		if (requestResponseRegistry == null)
			throw new NullPointerException("requestResponseRegistry");

		this.job = job;
		this.chat = chat;
		this.requestResponseRegistry = requestResponseRegistry;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		// @@@@@
		return false;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar) {
		// @@@@@
		// 상세페이지 url을 넘겨주면 보고보고에서 다운로드 링크를 넘겨주는 부분 만들어야 됨

      SendMessage answer = new SendMessage();
      answer.setChatId(chat.getId().toString());
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
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectedBoardItemRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}


/*

@Override
public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
	// @@@@@
	SendMessage answer = new SendMessage();
	answer.setChatId(chat.getId().toString());
	answer.setText("조회 할 게시판을 선택하세요.");

	ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
	forceReplyKeyboard.setSelective(true);

	answer.setReplyMarkup(getMainMenuKeyboard(""));

	try {
		absSender.sendMessage(answer);
	} catch (TelegramApiException e) {
		logger.error(null, e);
	}
}

private ReplyKeyboardMarkup getMainMenuKeyboard(String language) {
	ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
	replyKeyboardMarkup.setSelective(true);
	replyKeyboardMarkup.setResizeKeyboard(true);
	replyKeyboardMarkup.setOneTimeKeyboad(true);

	WebSiteBoard[] boardValues = this.site.getBoardValues();

	List<KeyboardRow> keyboard = new ArrayList<>();
	for (WebSiteBoard board : boardValues) {
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		keyboardFirstRow.add(String.format("선택완료 %s. %s", board.getCode(), board.getDescription()));
		
		keyboard.add(keyboardFirstRow);
	}
	
	replyKeyboardMarkup.setKeyboard(keyboard);

	return replyKeyboardMarkup;
}
*/
/*
// 키보드 숨기기 안됨
ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
replyKeyboardMarkup.setSelective(true);

List<KeyboardRow> keyboard = new ArrayList<>();
replyKeyboardMarkup.setKeyboard(keyboard);

answerMessage.setReplyMarkup(replyKeyboardMarkup);
*/

//@Override
//public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
//	System.out.println("############3");
//	// @@@@@
////	DatabaseManager databseManager = DatabaseManager.getInstance();
////    StringBuilder messageBuilder = new StringBuilder();
////
////    String userName = user.getFirstName() + " " + user.getLastName();
////
////    if (databseManager.getUserStateForCommandsBot(user.getId())) {
////        messageBuilder.append("Hi ").append(userName).append("\n");
////        messageBuilder.append("i think we know each other already!");
////    } else {
////        databseManager.setUserStateForCommandsBot(user.getId(), true);
////        messageBuilder.append("Welcome ").append(userName).append("\n");
////        messageBuilder.append("this bot will demonstrate you the command feature of the Java TelegramBots API!");
////    }
//
//    SendMessage answer = new SendMessage();
//    answer.setChatId(chat.getId().toString());
//    answer.setText("TEST1");
//    
//    ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
//    forceReplyKeyboard.setSelective(true);
//
////    answer.setReplyMarkup(getMainMenuKeyboard(""));
//    
//    InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
//    
//    List<InlineKeyboardButton> keyboard = new ArrayList<>();
//    InlineKeyboardButton keyboardFirstRow = new InlineKeyboardButton();
//    keyboardFirstRow.setText("TEXT lsadjfla sdfljasldf lasjdfl asdlfj alsdjf lasfja sldjflas dflasjdf lasdjfljaslf");
//    keyboardFirstRow.setCallbackData("data");
//    keyboard.add(keyboardFirstRow);
//
//    InlineKeyboardButton keyboardFirstRow2 = new InlineKeyboardButton();
//    keyboardFirstRow2.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
//    keyboardFirstRow2.setCallbackData("data2");
//    keyboard.add(keyboardFirstRow2);
//    
//    List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
//    InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
//    keyboardFirstRow3.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
//    keyboardFirstRow3.setCallbackData("data3");
//    keyboard2.add(keyboardFirstRow3);
//    
//    List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
//    keyboards.add(keyboard);
//    keyboards.add(keyboard2);
//
//	inline.setKeyboard(keyboards);
//    answer.setReplyMarkup(inline);
//    
//    try {
//        absSender.sendMessage(answer);
//    } catch (TelegramApiException e) {
//    }
////    
////    try {
////		Thread.sleep(5000);
////	} catch (InterruptedException e1) {
////		// TODO Auto-generated catch block
////		e1.printStackTrace();
////	}
////    
////    try {
////        absSender.sendMessage(answer);
////    } catch (TelegramApiException e) {
////    }
//}
//
//private static ReplyKeyboardMarkup getMainMenuKeyboard(String language) {
//    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//    replyKeyboardMarkup.setSelective(true);
//    replyKeyboardMarkup.setResizeKeyboard(true);
//    replyKeyboardMarkup.setOneTimeKeyboad(false);
//
//    List<KeyboardRow> keyboard = new ArrayList<>();
//    KeyboardRow keyboardFirstRow = new KeyboardRow();
//    keyboardFirstRow.add("a");
//    keyboardFirstRow.add("B");
//    KeyboardRow keyboardSecondRow = new KeyboardRow();
//    keyboardSecondRow.add("C");
//    keyboardSecondRow.add("D");
//    keyboard.add(keyboardFirstRow);
//    keyboard.add(keyboardSecondRow);
//    replyKeyboardMarkup.setKeyboard(keyboard);
//
//    return replyKeyboardMarkup;
//}
