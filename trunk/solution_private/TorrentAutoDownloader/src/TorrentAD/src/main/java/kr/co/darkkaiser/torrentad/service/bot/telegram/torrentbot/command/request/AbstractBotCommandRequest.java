package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;

// @@@@@
public abstract class AbstractBotCommandRequest extends AbstractRequest implements BotCommand {

    public final static String COMMAND_INIT_CHARACTER = "/";
    public static final String COMMAND_PARAMETER_SEPARATOR = " ";
    private final static int COMMAND_MAX_LENGTH = 32;

    private final String commandIdentifier;
    private final String description;
    
    public AbstractBotCommandRequest(String commandIdentifier, String description) {
    	this.commandIdentifier = commandIdentifier;
    	this.description = description;
    }
    
	@Override
	public String getCommandIdentifier() {
		return this.commandIdentifier;
	}
	
	@Override
	public String getCommandDescription() {
		return this.description;
	}

}
