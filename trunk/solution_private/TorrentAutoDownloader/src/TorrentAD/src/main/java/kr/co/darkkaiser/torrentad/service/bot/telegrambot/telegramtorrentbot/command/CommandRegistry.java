package kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.jsoup.helper.StringUtil;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;

public class CommandRegistry implements ICommandRegistry {

	private final Map<String, BotCommand> commands = new LinkedHashMap<>();

	@Override
	public void registerDefaultAction(BiConsumer<AbsSender, Message> unknownCommand) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean register(BotCommand botCommand) {
		if (botCommand == null)
			throw new NullPointerException("botCommand");

		if (this.commands.containsKey(botCommand.getCommandIdentifier()) == true) {
			return false;
		}
		
		this.commands.put(botCommand.getCommandIdentifier(), botCommand);
		
		return true;
	}

	@Override
	public final Map<BotCommand, Boolean> registerAll(BotCommand... botCommands) {
		if (botCommands == null)
			throw new NullPointerException("botCommands");

		Map<BotCommand, Boolean> resultMap = new HashMap<>(botCommands.length);
		
		for (BotCommand botCommand : botCommands) {
			resultMap.put(botCommand, register(botCommand));
		}
		
		return resultMap;
	}

	@Override
	public final boolean deregister(BotCommand botCommand) {
		if (botCommand == null)
			throw new NullPointerException("botCommand");

		if (this.commands.containsKey(botCommand.getCommandIdentifier()) == true) {
			this.commands.remove(botCommand.getCommandIdentifier());
			return true;
		}
		
		return false;
	}

	@Override
	public final Map<BotCommand, Boolean> deregisterAll(BotCommand... botCommands) {
		if (botCommands == null)
			throw new NullPointerException("botCommands");

		Map<BotCommand, Boolean> resultMap = new HashMap<>(botCommands.length);
		
		for (BotCommand botCommand : botCommands) {
			resultMap.put(botCommand, deregister(botCommand));
		}

		return resultMap;
	}

	@Override
	public final Collection<BotCommand> getRegisteredCommands() {
		return this.commands.values();
	}

	@Override
	public final BotCommand getRegisteredCommand(String commandIdentifier) {
		if (StringUtil.isBlank(commandIdentifier) == true)
			throw new IllegalArgumentException("commandIdentifier는 빈 문자열을 허용하지 않습니다.");

		return this.commands.get(commandIdentifier);
	}

	public final boolean executeCommand(AbsSender absSender, Message message) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (message == null)
			throw new NullPointerException("message");

		if (message.hasText() == true) {
			String commandMessage = message.getText();
			String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);

			String command = commandSplit[0];
			if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
				command = command.substring(1);

			if (commands.containsKey(command) == true) {
				String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
				commands.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
				return true;
			}
		}

		return false;
	}

}
