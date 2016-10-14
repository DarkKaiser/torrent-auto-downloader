package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

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

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestResponseRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;

public class SelectedBoardItemRequest extends AbstractRequest {

	private static final Logger logger = LoggerFactory.getLogger(SelectedBoardItemRequest.class);

	private final RequestResponseRegistry requestResponseRegistry;
	
	private final TorrentJob job;
	
	private final ChatRoom chat;
	
	public SelectedBoardItemRequest(RequestResponseRegistry requestResponseRegistry, TorrentJob job, ChatRoom chat) {
		super("게시물");
		
		if (requestResponseRegistry == null)
			throw new NullPointerException("requestResponseRegistry");

		this.job = job;
		this.chat = chat;
		this.requestResponseRegistry = requestResponseRegistry;
	}

	@Override
	public Response execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
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
//      
		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectedBoardItemRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
