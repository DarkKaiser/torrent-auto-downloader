package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

public class ListRequest extends BotCommandAbstractRequest {

	public ListRequest() {
		super("조회", "게시판을 조회합니다.");
	}
	
	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		// @@@@@
	}
	
	@Override
	public void cancel() {
		// @@@@@
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(ListRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
