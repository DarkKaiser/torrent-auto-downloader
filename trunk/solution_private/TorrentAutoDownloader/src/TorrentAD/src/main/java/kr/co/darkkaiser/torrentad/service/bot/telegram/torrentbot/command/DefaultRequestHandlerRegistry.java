package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.RequestHandler;

public final class DefaultRequestHandlerRegistry implements RequestHandlerRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRequestHandlerRegistry.class);
	
	private final Map<String, RequestHandler> handlerMap = new LinkedHashMap<>();

	@Override
	public final boolean register(RequestHandler handler) {
		if (handler == null)
			throw new NullPointerException("handler");

		if (this.handlerMap.containsKey(handler.getIdentifier()) == true)
			return false;

		this.handlerMap.put(handler.getIdentifier(), handler);
		
		return true;
	}

	@Override
	public final boolean deregister(RequestHandler handler) {
		if (handler == null)
			throw new NullPointerException("handler");

		if (this.handlerMap.containsKey(handler.getIdentifier()) == true) {
			this.handlerMap.remove(handler.getIdentifier());
			return true;
		}
		
		return false;
	}

	@Override
	public final Collection<RequestHandler> getRegisteredHandlers() {
		return this.handlerMap.values();
	}

	@Override
	public final RequestHandler getRegisteredHandler(String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		return this.handlerMap.get(identifier);
	}

	// @@@@@
	public RequestHandler getRequest(Update update) {
		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();

	            String commandMessage = message.getText();
				String[] commandMessageArrays = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);

				String command = commandMessageArrays[0];
				if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
					command = command.substring(1);

				return this.handlerMap.get(command);
	        }
		} catch (Exception e) {
			logger.error(null, e);
		}

		return null;
	}
	
	// @@@@@
	@Override
	public boolean execute(AbsSender absSender, Update update) {
		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();

	            String commandMessage = message.getText();
				String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);

				String command = commandSplit[0];
				if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
					command = command.substring(1);

				if (this.handlerMap.containsKey(command) == true) {
					String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
					this.handlerMap.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
					return true;
				}

	            // 검색어나 기타 다른것인지 확인
//	            Long chatId = message.getChatId();
	            // @@@@@
	        }

//			onCommandUnknownMessage(update);
		} catch (Exception e) {
			logger.error(null, e);
		}

		return false;
	}
//	// @@@@@
//	public final boolean executeCommand(AbsSender absSender, Message message) {
//		if (absSender == null)
//			throw new NullPointerException("absSender");
//		if (message == null)
//			throw new NullPointerException("message");
//
//		if (message.hasText() == true) {
//			String commandMessage = message.getText();
//			String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);
//
//			String command = commandSplit[0];
//			if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
//				command = command.substring(1);
//
//			if (commands.containsKey(command) == true) {
//				String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
//				commands.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
//				return true;
//			}
//		}
//
//		return false;
//	}

}
