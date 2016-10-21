package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;

public abstract class AbstractBotCommandRequestHandler extends AbstractRequestHandler implements BotCommand {

	private final String command;
	private final String commandDescription;

	public AbstractBotCommandRequestHandler(String command, String commandDescription) {
		super(command);

		if (StringUtil.isBlank(command) == true)
			throw new IllegalArgumentException("command는 빈 문자열을 허용하지 않습니다.");

		if (command.startsWith(BotCommandUtils.COMMAND_INITIAL_CHARACTER) == true)
			command = command.substring(1);

		if (command.length() > BotCommandUtils.COMMAND_MAX_LENGTH)
			throw new IllegalArgumentException("command의 길이는 최대 " + BotCommandUtils.COMMAND_MAX_LENGTH + "자 입니다.");

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

	protected boolean executable0(String command, String[] parameters, boolean requiredParameters) {
		if (getCommand().equals(command) == false)
			return false;

		if (requiredParameters == true) {
			if (parameters == null || parameters.length == 0)
				return false;
		} else {
			if (parameters != null && parameters.length > 0)
				return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractBotCommandRequestHandler.class.getSimpleName())
				.append("{")
				.append("command:").append(getCommand())
				.append(", commandDescription:").append(getCommandDescription())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
