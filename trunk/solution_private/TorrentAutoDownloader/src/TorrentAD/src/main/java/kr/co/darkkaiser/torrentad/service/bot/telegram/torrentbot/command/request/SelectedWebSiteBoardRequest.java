package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestResponseRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItem;

public class SelectedWebSiteBoardRequest extends AbstractRequest {

	private static final Logger logger = LoggerFactory.getLogger(SelectedWebSiteBoardRequest.class);

	private final RequestResponseRegistry requestResponseRegistry;
	
	private final TorrentJob job;
	
	private final ChatRoom chat;
	
	public SelectedWebSiteBoardRequest(RequestResponseRegistry requestResponseRegistry, TorrentJob job, ChatRoom chat) {
		super("선택완료");
		
		if (requestResponseRegistry == null)
			throw new NullPointerException("requestResponseRegistry");

		this.job = job;
		this.chat = chat;
		this.requestResponseRegistry = requestResponseRegistry;
	}

	@Override
	public Response execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
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

		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectedWebSiteBoardRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
