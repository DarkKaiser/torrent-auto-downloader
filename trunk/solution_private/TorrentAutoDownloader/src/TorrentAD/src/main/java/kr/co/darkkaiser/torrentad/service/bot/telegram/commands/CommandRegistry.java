package kr.co.darkkaiser.torrentad.service.bot.telegram.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;

public class CommandRegistry implements ICommandRegistry {

	private final Map<String, BotCommand> commands = new LinkedHashMap<>();

	private BiConsumer<AbsSender, Message> defaultConsumer;

	@Override
	public void registerDefaultAction(BiConsumer<AbsSender, Message> defaultConsumer) {
		this.defaultConsumer = defaultConsumer;
	}

	@Override
	public final boolean register(BotCommand botCommand) {
		if (this.commands.containsKey(botCommand.getCommandIdentifier()) == true) {
			return false;
		}
		
		this.commands.put(botCommand.getCommandIdentifier(), botCommand);
		
		return true;
	}

	@Override
	public final Map<BotCommand, Boolean> registerAll(BotCommand... botCommands) {
		Map<BotCommand, Boolean> resultMap = new HashMap<>(botCommands.length);
		for (BotCommand botCommand : botCommands) {
			resultMap.put(botCommand, register(botCommand));
		}
		
		return resultMap;
	}

	@Override
	public final boolean deregister(BotCommand botCommand) {
		if (this.commands.containsKey(botCommand.getCommandIdentifier()) == true) {
			this.commands.remove(botCommand.getCommandIdentifier());
			return true;
		}
		
		return false;
	}

	@Override
	public final Map<BotCommand, Boolean> deregisterAll(BotCommand... botCommands) {
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
		return this.commands.get(commandIdentifier);
	}

	public final boolean executeCommand(AbsSender absSender, Message message) {
		// @@@@@
		if (message.hasText()) {
			String commandMessage = message.getText();
			String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);

			String command = commandSplit[0];
			if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
				command = command.substring(1);

			if (commands.containsKey(command) == true) {
				String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
				commands.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
				return true;
			} else if (defaultConsumer != null) {
				// @@@@@ 명령이 아니면 검색어일수 있으므로 처리가되면 안됨
				defaultConsumer.accept(absSender, message);
				return true;
			}
		}

		return false;
	}

}
