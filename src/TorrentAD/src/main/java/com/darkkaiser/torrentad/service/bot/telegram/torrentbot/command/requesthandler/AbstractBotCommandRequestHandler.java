package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import lombok.Getter;
import org.jsoup.helper.StringUtil;

@Getter
public abstract class AbstractBotCommandRequestHandler extends AbstractRequestHandler implements BotCommand {

	private final String command;
	private final String commandKor;
	private final String commandSyntax;
	private final String commandDescription;

	public AbstractBotCommandRequestHandler(final String command) {
		this(command, "", "", "");
	}

	public AbstractBotCommandRequestHandler(final String command, final String commandKor, final String commandSyntax, final String commandDescription) {
		super(command);

		String _command = command;
		String _commandKor = commandKor;

		if (StringUtil.isBlank(_command) == true)
			throw new IllegalArgumentException("command는 빈 문자열을 허용하지 않습니다.");

		if (_command.startsWith(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER) == true)
			_command = _command.substring(1);
		if (_command.length() > BotCommandConstants.BOT_COMMAND_MAX_LENGTH)
			throw new IllegalArgumentException("command의 길이는 최대 " + BotCommandConstants.BOT_COMMAND_MAX_LENGTH + "자 입니다.");

		if (StringUtil.isBlank(_commandKor) == false) {
			if (_commandKor.startsWith(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER) == true)
				_commandKor = _commandKor.substring(1);
			if (_commandKor.length() > BotCommandConstants.BOT_COMMAND_MAX_LENGTH)
				throw new IllegalArgumentException("commandKor의 길이는 최대 " + BotCommandConstants.BOT_COMMAND_MAX_LENGTH + "자 입니다.");
		} else {
			_commandKor = "";
		}

		this.command = _command.toLowerCase();
		this.commandKor = _commandKor;
		this.commandSyntax = commandSyntax;
		this.commandDescription = commandDescription;
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
