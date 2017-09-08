package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import org.jsoup.helper.StringUtil;

public abstract class AbstractBotCommandRequestHandler extends AbstractRequestHandler implements BotCommand {

	private final String command;
	private final String commandKor;
	private final String commandSyntax;
	private final String commandDescription;

	public AbstractBotCommandRequestHandler(final String command) {
		this(command, "", "", "");
	}

	public AbstractBotCommandRequestHandler(String command, String commandKor, final String commandSyntax, final String commandDescription) {
		super(command);

		if (StringUtil.isBlank(command) == true)
			throw new IllegalArgumentException("command는 빈 문자열을 허용하지 않습니다.");

		if (command.startsWith(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER) == true)
			command = command.substring(1);
		if (command.length() > BotCommandConstants.BOT_COMMAND_MAX_LENGTH)
			throw new IllegalArgumentException("command의 길이는 최대 " + BotCommandConstants.BOT_COMMAND_MAX_LENGTH + "자 입니다.");

		if (StringUtil.isBlank(commandKor) == false) {
			if (commandKor.startsWith(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER) == true)
				commandKor = commandKor.substring(1);
			if (commandKor.length() > BotCommandConstants.BOT_COMMAND_MAX_LENGTH)
				throw new IllegalArgumentException("commandKor의 길이는 최대 " + BotCommandConstants.BOT_COMMAND_MAX_LENGTH + "자 입니다.");
		} else {
			commandKor = "";
		}

		this.command = command.toLowerCase();
		this.commandKor = commandKor;
		this.commandSyntax = commandSyntax;
		this.commandDescription = commandDescription;
	}

	@Override
	public String getCommand() {
		return this.command;
	}
	
	@Override
	public String getCommandKor() {
		return this.commandKor;
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
	protected boolean executable0(final String command, final String[] parameters, final boolean containInitialChar, final int minParametersCount, final int maxParametersCount) {
		// 명령 Check
		if (command.equals(getCommand()) == true) {
			if (containInitialChar == false)
				return false;
		} else {
			if (command.equals(getCommandKor()) == false)
				return false;
		}

		// 파라메터 갯수 Check
		int parametersCount = 0;
		if (parameters != null)
			parametersCount = parameters.length;

		return (minParametersCount == -1 || minParametersCount <= parametersCount)
				&& (maxParametersCount == -1 || maxParametersCount >= parametersCount);
	}
	
	@Override
	public String toString() {
		return AbstractBotCommandRequestHandler.class.getSimpleName() +
				"{" +
				"command:" + getCommand() +
				", commandKor:" + getCommandKor() +
				", commandSyntax:" + getCommandSyntax() +
				", commandDescription:" + getCommandDescription() +
				"}, " +
				super.toString();
	}

}
