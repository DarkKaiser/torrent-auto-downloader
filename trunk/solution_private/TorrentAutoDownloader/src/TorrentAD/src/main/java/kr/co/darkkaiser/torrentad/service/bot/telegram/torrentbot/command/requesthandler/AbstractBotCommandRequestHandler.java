package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;

public abstract class AbstractBotCommandRequestHandler extends AbstractRequestHandler implements BotCommand {

	private final String command;
	private final String commandSyntax;
	private final String commandDescription;
	
	public AbstractBotCommandRequestHandler(String command, String commandDescription) {
		this(command, command, commandDescription);
	}

	public AbstractBotCommandRequestHandler(String command, String commandSyntax, String commandDescription) {
		super(command);

		if (StringUtil.isBlank(command) == true)
			throw new IllegalArgumentException("command는 빈 문자열을 허용하지 않습니다.");

		if (command.startsWith(BotCommandUtils.BOT_COMMAND_INITIAL_CHARACTER) == true)
			command = command.substring(1);

		if (command.length() > BotCommandUtils.BOT_COMMAND_MAX_LENGTH)
			throw new IllegalArgumentException("command의 길이는 최대 " + BotCommandUtils.BOT_COMMAND_MAX_LENGTH + "자 입니다.");

		this.command = command.toLowerCase();
		this.commandSyntax = commandSyntax;
		this.commandDescription = commandDescription;
	}

	@Override
	public String getCommand() {
		return this.command;
	}
	
	@Override
	public String getCommandSyntax() {
		return this.commandSyntax;
	}

	@Override
	public String getCommandDescription() {
		return this.commandDescription;
	}

	@Override
	protected boolean executable0(String command, String[] parameters, int minParametersCount, int maxParametersCount) {
		if (getCommand().equals(command) == false)
			return false;

		int parametersCount = 0;
		if (parameters != null)
			parametersCount = parameters.length;

		if (minParametersCount <= parametersCount && parametersCount <= maxParametersCount)
			return true;

		return false;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractBotCommandRequestHandler.class.getSimpleName())
				.append("{")
				.append("command:").append(getCommand())
				.append(", commandSyntax:").append(getCommandSyntax())
				.append(", commandDescription:").append(getCommandDescription())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
