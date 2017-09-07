package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.ExposedBotCommand;

public class HelpRequestHandler extends AbstractBotCommandRequestHandler implements ExposedBotCommand {

	private final RequestHandlerRegistry requestHandlerRegistry;

	public HelpRequestHandler(RequestHandlerRegistry requestHandlerRegistry) {
		super("help", "도움", "/help (도움)", "도움말을 표시합니다.");

		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 0, 0) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

		for (RequestHandler handler : this.requestHandlerRegistry.getRequestHandlers()) {
			if (handler instanceof ExposedBotCommand) {
				ExposedBotCommand botCommand = (ExposedBotCommand) handler;
				sbAnswerMessage.append(botCommand.getCommandSyntax()).append("\n")
						.append(botCommand.getCommandDescription()).append("\n\n");
			}
		}

		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
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
