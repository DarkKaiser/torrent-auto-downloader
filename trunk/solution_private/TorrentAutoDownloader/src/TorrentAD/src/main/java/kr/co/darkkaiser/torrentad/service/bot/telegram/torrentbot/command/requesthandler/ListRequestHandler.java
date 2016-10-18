package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;

public class ListRequestHandler extends AbstractBotCommandRequestHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ListRequestHandler.class);
	
	private ChatRoom chat;
	
	private TorrentJob job;

	// @@@@@
	// 조회 [갯수]
	public ListRequestHandler(TorrentJob job, ChatRoom chat) {
		super("조회", "게시판을 조회합니다.");
		
		this.job = job;
		this.chat = chat;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		if (this.chat.getBoard() == null) {
			StringBuilder sbMessage = new StringBuilder();
			sbMessage.append("게시판을 먼저 선택하세요.");

			SendMessage helpMessage = new SendMessage()
					.setChatId(chat.getId().toString())
					.setText(sbMessage.toString())
					.enableHtml(true);

			try {
				absSender.sendMessage(helpMessage);
			} catch (TelegramApiException e) {
				logger.error(null, e);
			}

			return;
		}

		StringBuilder sbMessage = new StringBuilder();
		sbMessage.append(String.format("%s 게시판 조회중입니다.", this.chat.getBoard().getDescription()));

		SendMessage helpMessage = new SendMessage()
				.setChatId(chat.getId().toString())
				.setText(sbMessage.toString())
				.enableHtml(true);

		try {
			absSender.sendMessage(helpMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
		
		this.job.list(absSender, user, chat);
		
		// @@@@@
	}

//	@Override
//	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
//		System.out.println("############3");
//		// @@@@@
////		DatabaseManager databseManager = DatabaseManager.getInstance();
////        StringBuilder messageBuilder = new StringBuilder();
////
////        String userName = user.getFirstName() + " " + user.getLastName();
////
////        if (databseManager.getUserStateForCommandsBot(user.getId())) {
////            messageBuilder.append("Hi ").append(userName).append("\n");
////            messageBuilder.append("i think we know each other already!");
////        } else {
////            databseManager.setUserStateForCommandsBot(user.getId(), true);
////            messageBuilder.append("Welcome ").append(userName).append("\n");
////            messageBuilder.append("this bot will demonstrate you the command feature of the Java TelegramBots API!");
////        }
//
//        SendMessage answer = new SendMessage();
//        answer.setChatId(chat.getId().toString());
//        answer.setText("TEST1");
//        
//        ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
//        forceReplyKeyboard.setSelective(true);
//
////        answer.setReplyMarkup(getMainMenuKeyboard(""));
//        
//        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
//        
//        List<InlineKeyboardButton> keyboard = new ArrayList<>();
//        InlineKeyboardButton keyboardFirstRow = new InlineKeyboardButton();
//        keyboardFirstRow.setText("TEXT lsadjfla sdfljasldf lasjdfl asdlfj alsdjf lasfja sldjflas dflasjdf lasdjfljaslf");
//        keyboardFirstRow.setCallbackData("data");
//        keyboard.add(keyboardFirstRow);
//
//        InlineKeyboardButton keyboardFirstRow2 = new InlineKeyboardButton();
//        keyboardFirstRow2.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
//        keyboardFirstRow2.setCallbackData("data2");
//        keyboard.add(keyboardFirstRow2);
//        
//        List<InlineKeyboardButton> keyboard2 = new ArrayList<>();
//        InlineKeyboardButton keyboardFirstRow3 = new InlineKeyboardButton();
//        keyboardFirstRow3.setText("TEXT asjdfl asdlfjlas dfa sdlfj2");
//        keyboardFirstRow3.setCallbackData("data3");
//        keyboard2.add(keyboardFirstRow3);
//        
//        List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();
//        keyboards.add(keyboard);
//        keyboards.add(keyboard2);
//
//		inline.setKeyboard(keyboards);
//        answer.setReplyMarkup(inline);
//        
//        try {
//            absSender.sendMessage(answer);
//        } catch (TelegramApiException e) {
//        }
////        
////        try {
////			Thread.sleep(5000);
////		} catch (InterruptedException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////        
////        try {
////            absSender.sendMessage(answer);
////        } catch (TelegramApiException e) {
////        }
//	}
//	
//	private static ReplyKeyboardMarkup getMainMenuKeyboard(String language) {
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboad(false);
//
//        List<KeyboardRow> keyboard = new ArrayList<>();
//        KeyboardRow keyboardFirstRow = new KeyboardRow();
//        keyboardFirstRow.add("a");
//        keyboardFirstRow.add("B");
//        KeyboardRow keyboardSecondRow = new KeyboardRow();
//        keyboardSecondRow.add("C");
//        keyboardSecondRow.add("D");
//        keyboard.add(keyboardFirstRow);
//        keyboard.add(keyboardSecondRow);
//        replyKeyboardMarkup.setKeyboard(keyboard);
//
//        return replyKeyboardMarkup;
//    }

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ListRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
