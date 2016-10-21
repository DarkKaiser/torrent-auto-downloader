package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import java.util.ArrayList;
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

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;

//@@@@@
public class SelectedWebSiteBoardRequestHandler extends AbstractRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(SelectedWebSiteBoardRequestHandler.class);

	private final RequestHandlerRegistry requestResponseRegistry;
	
	private final TorrentJob job;
	
//	private final WebSite webSite; job 보다는 이걸 받아야 됨
	
	private final ChatRoom chat;
	
	public SelectedWebSiteBoardRequestHandler(RequestHandlerRegistry requestResponseRegistry, TorrentJob job, ChatRoom chat) {
		super("선택완료");
		
		if (requestResponseRegistry == null)
			throw new NullPointerException("requestResponseRegistry");

		this.job = job;
		this.chat = chat;
		this.requestResponseRegistry = requestResponseRegistry;
	}
	
	@Override
	public boolean executable(String command, String[] parameters) {
		// @@@@@
		return false;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		// @@@@@
		WebSiteBoard board = BogoBogoBoard.ANI_ON;
		this.chat.setBoard(board);
		
		SendMessage answer = new SendMessage();
		answer.setChatId(chat.getId().toString());
		answer.setText(String.format("%s 게시판이 선택되었습니다.", board.getDescription()));
		
		// 키보드 숨기기 안됨
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		
		List<KeyboardRow> keyboard = new ArrayList<>();
		replyKeyboardMarkup.setKeyboard(keyboard);
		
		answer.setReplyMarkup(replyKeyboardMarkup);
		
		try {
			absSender.sendMessage(answer);
		} catch (TelegramApiException e) {
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectedWebSiteBoardRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
