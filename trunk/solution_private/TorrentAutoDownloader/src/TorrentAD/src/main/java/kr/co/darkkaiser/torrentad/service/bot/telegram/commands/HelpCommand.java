package kr.co.darkkaiser.torrentad.service.bot.telegram.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;

public class HelpCommand extends BotCommand {

	private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

	private final ICommandRegistry commandRegistry;

	public HelpCommand(ICommandRegistry commandRegistry) {
		super("help", "도움말을 표시합니다.");

		this.commandRegistry = commandRegistry;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
		StringBuilder helpMessageBuilder = new StringBuilder();
		helpMessageBuilder.append("입력하실 수 있는 명령어는 다음과 같습니다:\n\n");

		for (BotCommand botCommand : commandRegistry.getRegisteredCommands()) {
			helpMessageBuilder.append(botCommand.toString()).append("\n\n");
		}

		SendMessage helpMessage = new SendMessage();
		helpMessage.setChatId(chat.getId().toString());
		helpMessage.enableHtml(true);
		helpMessage.setText(helpMessageBuilder.toString());

		try {
			absSender.sendMessage(helpMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

}
