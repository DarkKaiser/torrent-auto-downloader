package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Arrays;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.util.OutParam;

public final class BotCommandUtils {

	// TelegramBot 커맨드의 최대 길이
	public static final int COMMAND_MAX_LENGTH = 32;

	// TelegramBot 커맨드 이니셜 문자
	public static final String COMMAND_INITIAL_CHARACTER = "/";

	// TelegramBot 커맨드 또는 파라메터간의 구분자
    public static final String COMMAND_PARAMETER_SEPARATOR = " ";

    // 하나의 커맨드에 파라메터가 포함되어 있는 형식을 가지는 OneComplexCommand의 구분자
    public static final String ONE_COMPLEX_COMMAND_SEPARATOR = "_";

    public static final void parse(String message, final OutParam<String> outCommand, final OutParam<String[]> outParameters, final OutParam<Boolean> outContainInitialChar) {
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");
		if (outCommand == null)
			throw new NullPointerException("outCommand");
		if (outParameters == null)
			throw new NullPointerException("outParameters");

		String[] messageArrays = message.split(BotCommandUtils.COMMAND_PARAMETER_SEPARATOR);

		String command = messageArrays[0];
		if (command.startsWith(BotCommandUtils.COMMAND_INITIAL_CHARACTER) == true) {
			command = command.substring(1);

			outContainInitialChar.set(true);
		} else {
			outContainInitialChar.set(false);
		}

		// OneComplexCommand인지 확인한다.
		if (messageArrays.length == 1) {
			String[] commandArrays = command.split(ONE_COMPLEX_COMMAND_SEPARATOR);
			if (commandArrays.length > 1) {
				outCommand.set(commandArrays[0]);
				outParameters.set(Arrays.copyOfRange(commandArrays, 1, commandArrays.length));
				return;
			}
		}

		outCommand.set(command);
		outParameters.set(Arrays.copyOfRange(messageArrays, 1, messageArrays.length));
    }
    
    // @@@@@ 이 함수를 여기다 둬야하나???
    // 가변인자를 둬서 들어온 순서대로 만들어주는건??? 이 함수를 이용하는 쪽에서 순서나 그런건 정함
    public static final String generateCommand(String... strs) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("/");
    	
    	for (String str : strs) {
    		sb.append(str).append(ONE_COMPLEX_COMMAND_SEPARATOR);
    	}
    	
    	sb.delete(sb.length() - 1, sb.length());
    	
    	return sb.toString();
    }

	private BotCommandUtils() {
	}

}
