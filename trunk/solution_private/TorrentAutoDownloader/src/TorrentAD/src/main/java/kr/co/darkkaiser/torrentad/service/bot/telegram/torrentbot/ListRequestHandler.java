package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.AbstractBotCommandRequestHandler;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;

public class ListRequestHandler extends AbstractBotCommandRequestHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ListRequestHandler.class);
	
	// 설정 가능한 최소/최대 조회 건수@@@@@ 변수명 조정
	public static final int MIN_INQUIRY_LIST_COUNT = 5;
	public static final int MAX_INQUIRY_LIST_COUNT = 50;

	//@@@@@
	private ChatRoom chat;
	
	//@@@@@
	private TorrentJob job;

	public ListRequestHandler(TorrentJob job, ChatRoom chat) {
		super("조회", "조회 [건수]", "선택된 게시판을 조회합니다.");
		
		// @@@@@
		this.job = job;
		// @@@@@
		this.chat = chat;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 0, 1) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String command, String[] parameters, boolean containInitialChar) {
//		if (this.chat.getBoard() == null) {
//			StringBuilder sbMessage = new StringBuilder();
//			sbMessage.append("조회 및 검색하려는 게시판을 먼저 선택하세요.");
//
//			SendMessage helpMessage = new SendMessage()
//					.setChatId(chat.getId().toString())
//					.setText(sbMessage.toString())
//					.enableHtml(true);
//
//			try {
//				absSender.sendMessage(helpMessage);
//			} catch (TelegramApiException e) {
//				logger.error(null, e);
//			}
//
//			return;
//		}
		//////////////////////////////////////////

		// 입력된 조회 건수를 확인하고, 설정값을 저장한다.
		if (parameters != null) {
			try {
				int listCount = Integer.parseInt(parameters[0]);
				if (listCount < MIN_INQUIRY_LIST_COUNT || listCount > MAX_INQUIRY_LIST_COUNT) {
					StringBuilder sbMessageText = new StringBuilder();
					sbMessageText.append("설정 가능한 조회 건수는 최소 ").append(MIN_INQUIRY_LIST_COUNT).append("건에서 최대 ").append(MAX_INQUIRY_LIST_COUNT).append("건 입니다.");
					sendAnswerMessage(absSender, user, chat, sbMessageText.toString());
					return;
				}

				// @@@@@ 조회건수를 저장한다.
			} catch (NumberFormatException e) {
				sendAnswerMessage(absSender, user, chat, "조회 건수는 숫자만 입력 가능합니다.");
				return;
			}
		}
		
		//////////////////////////////////////////

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

	private void sendAnswerMessage(AbsSender absSender, User user, Chat chat, String messageText) {
		SendMessage answerMessage = new SendMessage()
				.setChatId(chat.getId().toString())
				.setText(messageText)
				.enableHtml(true);

		try {
			absSender.sendMessage(answerMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

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
