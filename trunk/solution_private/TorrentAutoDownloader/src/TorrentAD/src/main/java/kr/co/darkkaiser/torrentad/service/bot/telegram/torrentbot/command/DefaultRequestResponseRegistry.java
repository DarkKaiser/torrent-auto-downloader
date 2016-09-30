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

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public final class DefaultRequestResponseRegistry implements RequestResponseRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRequestResponseRegistry.class);
	
	private final Map<String, Request> requestMap = new LinkedHashMap<>();
	private final Map<String, Response> responseMap = new LinkedHashMap<>();
	
	@Override
	public final boolean register(Request request) {
		if (request == null)
			throw new NullPointerException("request");

		if (this.requestMap.containsKey(request.getIdentifier()) == true)
			return false;

		this.requestMap.put(request.getIdentifier(), request);
		
		return true;
	}

	@Override
	public final boolean deregister(Request request) {
		if (request == null)
			throw new NullPointerException("request");

		if (this.requestMap.containsKey(request.getIdentifier()) == true) {
			this.requestMap.remove(request.getIdentifier());
			return true;
		}
		
		return false;
	}

	@Override
	public final boolean register(Response response) {
		if (response == null)
			throw new NullPointerException("response");

		if (this.responseMap.containsKey(response.getIdentifier()) == true)
			return false;
		
		this.responseMap.put(response.getIdentifier(), response);
		
		return true;
	}

	@Override
	public final boolean deregister(Response response) {
		if (response == null)
			throw new NullPointerException("response");

		if (this.responseMap.containsKey(response.getIdentifier()) == true) {
			this.responseMap.remove(response.getIdentifier());
			return true;
		}
		
		return false;
	}

	@Override
	public final Collection<Request> getRegisteredRequests() {
		return this.requestMap.values();
	}

	@Override
	public final Collection<Response> getRegisteredResponses() {
		return this.responseMap.values();
	}

	@Override
	public final Request getRegisteredRequest(String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		return this.requestMap.get(identifier);
	}

	@Override
	public final Response getRegisteredResponse(String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		return this.responseMap.get(identifier);
	}
	
	public Request get(Update update) {
		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();

	            String commandMessage = message.getText();
				String[] commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR);

				String command = commandSplit[0];
				if (command.startsWith(BotCommand.COMMAND_INIT_CHARACTER) == true)
					command = command.substring(1);

				if (this.requestMap.containsKey(command) == true) {
					String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
//					this.requestMap.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
					return this.requestMap.get(command);
				}

	            // 검색어나 기타 다른것인지 확인
	            Long chatId = message.getChatId();
	            // @@@@@
	        }

//			onCommandUnknownMessage(update);
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

				if (this.requestMap.containsKey(command) == true) {
					String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
					this.requestMap.get(command).execute(absSender, message.getFrom(), message.getChat(), parameters);
					return true;
				}

	            // 검색어나 기타 다른것인지 확인
	            Long chatId = message.getChatId();
	            // @@@@@
	        }

//			onCommandUnknownMessage(update);
		} catch (Exception e) {
			logger.error(null, e);
		}

		return false;
	}

}
