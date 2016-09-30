package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

public class HelpRequest extends BotCommandAbstractRequest {

	public HelpRequest() {
		super("도움", "도움말을 표시합니다.");
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
				.append(HelpRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
