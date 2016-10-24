package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

public class TorrentStatusRequestHandler extends AbstractBotCommandRequestHandler {
	
	public TorrentStatusRequestHandler() {
		super("상태", "토렌트 서버의 다운로드 상태를 조회합니다.");
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 0, 0) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar) {
		sendAnswerMessage(absSender, chat.getId().toString(), "토렌트 서버의 다운로드 상태를 조회중입니다.\n잠시만 기다려 주세요.");

		// @@@@@
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(TorrentStatusRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
