package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.ExposedBotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Objects;

public class HelpRequestHandler extends AbstractBotCommandRequestHandler implements ExposedBotCommand {

	private final RequestHandlerRegistry requestHandlerRegistry;

	public HelpRequestHandler(final RequestHandlerRegistry requestHandlerRegistry) {
		super("help", "도움", "/help (도움)", "도움말을 표시합니다.");

		Objects.requireNonNull(requestHandlerRegistry, "requestHandlerRegistry");

		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		return super.executable0(command, parameters, containInitialChar, 0, 0) != false;
	}

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

		for (final RequestHandler handler : this.requestHandlerRegistry.getRequestHandlers()) {
			if (handler instanceof ExposedBotCommand botCommand) {
				sbAnswerMessage.append(botCommand.getCommandSyntax()).append("\n")
						.append(botCommand.getCommandDescription()).append("\n\n");
			}
		}

		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
	}

	@Override
	public String toString() {
		return HelpRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
