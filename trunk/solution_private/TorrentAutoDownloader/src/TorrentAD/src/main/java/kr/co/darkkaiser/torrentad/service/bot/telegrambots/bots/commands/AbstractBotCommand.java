package kr.co.darkkaiser.torrentad.service.bot.telegrambots.bots.commands;

import org.telegram.telegrambots.bots.commands.BotCommand;

public abstract class AbstractBotCommand extends BotCommand {

	public AbstractBotCommand(String commandIdentifier, String description) {
		super(commandIdentifier, description);
	}

	@Override
	public String toString() {
		return "<b>" + getCommandIdentifier() + "</b>\n" + getDescription();
	}

}
