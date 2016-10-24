package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.TorrentJob;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardListRequestHandler extends AbstractBotCommandRequestHandler {
	
	// 설정 가능한 최소/최대 조회 건수
	public static final int MIN_BOARD_ITEMS_LIST_COUNT = 5;
	public static final int MAX_BOARD_ITEMS_LIST_COUNT = 50;
	public static final int DEFAULT_BOARD_ITEMS_LIST_COUNT = MAX_BOARD_ITEMS_LIST_COUNT;

	//@@@@@
//	private TorrentJob job;

	public WebSiteBoardListRequestHandler(TorrentJob job) {
		super("조회", "조회 [건수]", "선택된 게시판을 조회합니다.");
		
		// @@@@@
//		this.job = job;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 0, 1) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar) {
		if (parameters != null && parameters.length > 0) {
			// 입력된 조회 건수를 확인하고, 설정값을 저장한다.
			try {
				int listCount = Integer.parseInt(parameters[0]);
				if (listCount < MIN_BOARD_ITEMS_LIST_COUNT || listCount > MAX_BOARD_ITEMS_LIST_COUNT) {
					StringBuilder sbAnswerMessage = new StringBuilder();
					sbAnswerMessage.append("입력된 조회 건수가 유효하지 않습니다.\n")
							.append("설정 가능한 조회 건수는 최소 ").append(MIN_BOARD_ITEMS_LIST_COUNT).append("건에서 최대 ").append(MAX_BOARD_ITEMS_LIST_COUNT).append("건 입니다.");

					sendAnswerMessage(absSender, chat.getId().toString(), sbAnswerMessage.toString());

					return;
				}

				chatRoom.setMaxBoardItemsListCount(listCount);
			} catch (NumberFormatException e) {
				sendAnswerMessage(absSender, chat.getId().toString(), "입력된 조회 건수가 유효하지 않습니다.\n조회 건수는 숫자만 입력 가능합니다.");
				return;
			}
		}
		
		// 조회할 게시판이 선택되었는지 확인한다.
		WebSiteBoard board = chatRoom.getBoard();
		if (board == null) {
			sendAnswerMessage(absSender, chat.getId().toString(), "조회 및 검색하려는 게시판을 먼저 선택하세요.");
			return;
		}

		// 게시판 조회중 메시지를 보낸다.
		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판을 최대 ").append(chatRoom.getMaxBoardItemsListCount()).append("건 조회중입니다.\n")
				.append("잠시만 기다려 주세요.");

		sendAnswerMessage(absSender, chat.getId().toString(), sbAnswerMessage.toString());

		// @@@@@
//		this.job.list(absSender, user, chat);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardListRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
