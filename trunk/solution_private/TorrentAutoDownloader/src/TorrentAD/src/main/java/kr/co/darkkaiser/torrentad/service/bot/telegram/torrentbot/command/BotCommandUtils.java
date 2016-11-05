package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Arrays;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.util.OutParam;

public final class BotCommandUtils {

	// TelegramBot 커맨드의 최대 길이
	public static final int BOT_COMMAND_MAX_LENGTH = 32;

	// TelegramBot 커맨드 이니셜 문자
	public static final String BOT_COMMAND_INITIAL_CHARACTER = "/";

	// TelegramBot 커맨드 또는 파라메터간의 구분자
    public static final String BOT_COMMAND_PARAMETER_SEPARATOR = " ";

    // 커맨드에 파라메터가 포함되어 있는 형식을 가지는 ComplexBotCommand의 구분자
    public static final String COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR = "_";

    public static final void parse(String message, final OutParam<String> outCommand, final OutParam<String[]> outParameters, final OutParam<Boolean> outContainInitialChar) {
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");
		if (outCommand == null)
			throw new NullPointerException("outCommand");
		if (outParameters == null)
			throw new NullPointerException("outParameters");

		String[] messageArrays = message.split(BotCommandUtils.BOT_COMMAND_PARAMETER_SEPARATOR);

		String command = messageArrays[0];
		if (command.startsWith(BotCommandUtils.BOT_COMMAND_INITIAL_CHARACTER) == true) {
			command = command.substring(1);

			outContainInitialChar.set(true);
		} else {
			outContainInitialChar.set(false);
		}

		// ComplexBotCommand인지 확인한다.
		if (messageArrays.length == 1) {
			String[] commandArrays = command.split(COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR);
			if (commandArrays.length > 1) {
				outCommand.set(commandArrays[0]);
				outParameters.set(Arrays.copyOfRange(commandArrays, 1, commandArrays.length));
				return;
			}
		}

		outCommand.set(command);
		outParameters.set(Arrays.copyOfRange(messageArrays, 1, messageArrays.length));
    }

    public static final String toComplexBotCommandString(String... args) {
    	StringBuilder sbComplexBotCommand = new StringBuilder()
    			.append(BOT_COMMAND_INITIAL_CHARACTER);

    	for (String argument : args) {
    		sbComplexBotCommand.append(argument).append(COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR);
    	}

    	return sbComplexBotCommand.delete(sbComplexBotCommand.length() - 1, sbComplexBotCommand.length())
    			.toString();
    }

	private BotCommandUtils() {
	}

}
