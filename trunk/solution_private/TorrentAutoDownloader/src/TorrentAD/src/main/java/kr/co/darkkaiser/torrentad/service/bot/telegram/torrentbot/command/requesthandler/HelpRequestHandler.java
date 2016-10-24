package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;

public class HelpRequestHandler extends AbstractBotCommandRequestHandler {

	private final RequestHandlerRegistry requestHandlerRegistry;

	public HelpRequestHandler(RequestHandlerRegistry requestHandlerRegistry) {
		super("도움", "도움말을 표시합니다.");

		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 0, 0) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar) {
		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

		for (RequestHandler handler : this.requestHandlerRegistry.getRequestHandlers()) {
			if (handler instanceof BotCommand) {
				BotCommand botCommand = (BotCommand) handler;
				sbAnswerMessage.append("<b>").append(botCommand.getCommandSyntax()).append("</b>\n")
						.append(botCommand.getCommandDescription()).append("\n\n");
			}
		}

		sendAnswerMessage(absSender, chat.getId().toString(), sbAnswerMessage.toString());
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(HelpRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
