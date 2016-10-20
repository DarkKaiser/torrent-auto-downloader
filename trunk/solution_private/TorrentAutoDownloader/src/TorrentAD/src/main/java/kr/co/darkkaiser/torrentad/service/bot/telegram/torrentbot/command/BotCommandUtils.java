package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Arrays;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.util.OutParam;

public final class BotCommandUtils {

	public static final int COMMAND_MAX_LENGTH = 32;
	public static final String COMMAND_INITIAL_CHARACTER = "/";
    public static final String COMMAND_PARAMETER_SEPARATOR = " ";

    public static final void parse(String message, final OutParam<String> outCommand, final OutParam<String[]> outParameters) {
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");
		if (outCommand == null)
			throw new NullPointerException("outCommand");
		if (outParameters == null)
			throw new NullPointerException("outParameters");

		String[] messageArrays = message.split(BotCommandUtils.COMMAND_PARAMETER_SEPARATOR);

		String command = messageArrays[0];
		if (command.startsWith(BotCommandUtils.COMMAND_INITIAL_CHARACTER) == true)
			command = command.substring(1);

		outCommand.set(command);
		outParameters.set(Arrays.copyOfRange(messageArrays, 1, messageArrays.length));
    }

	private BotCommandUtils() {
	}

}
