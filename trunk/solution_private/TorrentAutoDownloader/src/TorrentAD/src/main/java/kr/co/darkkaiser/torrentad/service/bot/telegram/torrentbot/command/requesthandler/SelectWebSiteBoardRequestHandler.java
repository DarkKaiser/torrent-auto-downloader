package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

// @@@@@
public class SelectWebSiteBoardRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(SelectWebSiteBoardRequestHandler.class);

	private final RequestHandlerRegistry requestHandlerRegistry;
	
	private final TorrentJob job;
	
	private final ChatRoom chat;
	
	public SelectWebSiteBoardRequestHandler(RequestHandlerRegistry requestResponseRegistry, TorrentJob job, ChatRoom chat) {
		super("선택", "조회 및 검색하려는 게시판을 선택합니다.");

		if (requestResponseRegistry == null)
			throw new NullPointerException("requestResponseRegistry");

		this.job = job;
		this.chat = chat;
		this.requestHandlerRegistry = requestResponseRegistry;
	}
	
	@Override
	public boolean executable(String command, String[] parameters) {
		if (super.executable0(command, parameters, false) == false)
			return false;

		return true;
	}

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
		
		WebSiteBoard[] boardValues = this.job.connector.getSite().getBoardValues();
		
		List<KeyboardRow> keyboard = new ArrayList<>();
		for (WebSiteBoard board : boardValues) {
			KeyboardRow keyboardFirstRow = new KeyboardRow();
			keyboardFirstRow.add("선택완료 1" + board.getDescription());
			
			keyboard.add(keyboardFirstRow);
		}
		
		replyKeyboardMarkup.setKeyboard(keyboard);

		return replyKeyboardMarkup;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectWebSiteBoardRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
