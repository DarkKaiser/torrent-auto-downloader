package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Arrays;

public final class BotCommandUtils {

	public static final int COMMAND_MAX_LENGTH = 32;
	public static final String COMMAND_INITIAL_CHARACTER = "/";
    public static final String COMMAND_PARAMETER_SEPARATOR = " ";
    
    // @@@@@ 2개를 반환해야됨
    public static final String test(String commandMessage) {
		String[] commandMessageArrays = commandMessage.split(BotCommandUtils.COMMAND_PARAMETER_SEPARATOR);

		String command = commandMessageArrays[0];
		if (command.startsWith(BotCommandUtils.COMMAND_INITIAL_CHARACTER) == true)
			command = command.substring(1);
		
		String[] parameters = Arrays.copyOfRange(commandMessageArrays, 1, commandMessageArrays.length);
		
		return command;
    }

	private BotCommandUtils() {
	}

}
