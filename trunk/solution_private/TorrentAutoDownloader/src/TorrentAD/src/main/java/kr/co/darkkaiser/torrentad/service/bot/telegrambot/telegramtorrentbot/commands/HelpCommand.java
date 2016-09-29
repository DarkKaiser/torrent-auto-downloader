package kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;

public class HelpCommand extends AbstractBotCommand {

	private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

	private final ICommandRegistry commandRegistry;

	public HelpCommand(ICommandRegistry commandRegistry) {
		super("도움", "도움말을 표시합니다.");

		this.commandRegistry = commandRegistry;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
		StringBuilder sbHelpMessage = new StringBuilder();
		sbHelpMessage.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

		for (BotCommand botCommand : this.commandRegistry.getRegisteredCommands()) {
			sbHelpMessage.append(botCommand.toString()).append("\n\n");
		}

		SendMessage helpMessage = new SendMessage();
		helpMessage.setChatId(chat.getId().toString());
		helpMessage.setText(sbHelpMessage.toString());
		helpMessage.enableHtml(true);

		try {
			absSender.sendMessage(helpMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

}
