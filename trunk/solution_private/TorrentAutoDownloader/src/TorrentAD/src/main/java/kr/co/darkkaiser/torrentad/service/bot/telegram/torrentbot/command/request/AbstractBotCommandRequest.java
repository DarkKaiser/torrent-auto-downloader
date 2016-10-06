package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;

public abstract class AbstractBotCommandRequest extends AbstractRequest implements BotCommand {

	private static final int COMMAND_MAX_LENGTH = 32;
	private static final String COMMAND_INIT_CHARACTER = "/";

	private final String command;
	private final String commandDescription;

	public AbstractBotCommandRequest(String identifier, String command, String commandDescription) {
		super(identifier);

		if (StringUtil.isBlank(command) == true)
			throw new IllegalArgumentException("command는 빈 문자열을 허용하지 않습니다.");

		if (command.startsWith(COMMAND_INIT_CHARACTER) == true)
			command = command.substring(1);

		if (command.length() > COMMAND_MAX_LENGTH)
			throw new IllegalArgumentException("command의 길이는 최대 " + COMMAND_MAX_LENGTH + "자 입니다.");

		this.command = command.toLowerCase();
		this.commandDescription = commandDescription;
	}

	@Override
	public String getCommand() {
		return this.command;
	}

	@Override
	public String getCommandDescription() {
		return this.commandDescription;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractBotCommandRequest.class.getSimpleName())
				.append("{")
				.append("command:").append(getCommand())
				.append(", commandDescription:").append(getCommandDescription())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
