package kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.commands;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;

public class ListCommand extends AbstractBotCommand {

	public ListCommand() {
		super("검색", "토렌트 검색을 시작합니다.");
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		System.out.println("############3");
		// @@@@@
//		DatabaseManager databseManager = DatabaseManager.getInstance();
//        StringBuilder messageBuilder = new StringBuilder();
//
//        String userName = user.getFirstName() + " " + user.getLastName();
//
//        if (databseManager.getUserStateForCommandsBot(user.getId())) {
//            messageBuilder.append("Hi ").append(userName).append("\n");
//            messageBuilder.append("i think we know each other already!");
//        } else {
//            databseManager.setUserStateForCommandsBot(user.getId(), true);
//            messageBuilder.append("Welcome ").append(userName).append("\n");
//            messageBuilder.append("this bot will demonstrate you the command feature of the Java TelegramBots API!");
//        }

        SendMessage answer = new SendMessage();
        answer.setChatId(chat.getId().toString());
        answer.setText("TEST1");
        
        ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
        forceReplyKeyboard.setSelective(true);

        answer.setReplyMarkup(getMainMenuKeyboard(""));

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
        }
//        
//        try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        
//        try {
//            absSender.sendMessage(answer);
//        } catch (TelegramApiException e) {
//        }
	}
	
	private static ReplyKeyboardMarkup getMainMenuKeyboard(String language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("a");
        keyboardFirstRow.add("B");
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("C");
        keyboardSecondRow.add("D");
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

}
