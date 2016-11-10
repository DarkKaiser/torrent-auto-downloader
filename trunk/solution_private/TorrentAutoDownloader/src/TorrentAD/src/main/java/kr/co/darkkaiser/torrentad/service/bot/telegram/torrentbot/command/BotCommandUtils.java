package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import java.util.Arrays;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.util.OutParam;

public final class BotCommandUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(BotCommandUtils.class);
	
    public static final void parseBotCommand(String message, final OutParam<String> outCommand, final OutParam<String[]> outParameters, final OutParam<Boolean> outContainInitialChar) {
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");
		if (outCommand == null)
			throw new NullPointerException("outCommand");
		if (outParameters == null)
			throw new NullPointerException("outParameters");

		String[] messageArrays = message.split(BotCommandConstants.BOT_COMMAND_PARAMETER_SEPARATOR);

		String command = messageArrays[0];
		if (command.startsWith(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER) == true) {
			command = command.substring(1);

			outContainInitialChar.set(true);
		} else {
			outContainInitialChar.set(false);
		}

		// ComplexBotCommand인지 확인한다.
		if (messageArrays.length == 1) {
			String[] commandArrays = command.split(BotCommandConstants.COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR);
			if (commandArrays.length > 1) {
				outCommand.set(commandArrays[0]);
				outParameters.set(Arrays.copyOfRange(commandArrays, 1, commandArrays.length));
				return;
			}
		}

		outCommand.set(command);
		outParameters.set(Arrays.copyOfRange(messageArrays, 1, messageArrays.length));
    }

    public static final String toComplexBotCommandString(String... args) {
    	StringBuilder sbComplexBotCommand = new StringBuilder()
    			.append(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER);

    	for (String argument : args) {
    		sbComplexBotCommand.append(argument).append(BotCommandConstants.COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR);
    	}

    	return sbComplexBotCommand.delete(sbComplexBotCommand.length() - 1, sbComplexBotCommand.length())
    			.toString();
    }

	public static void sendMessage(AbsSender absSender, long chatId, String message) {
		sendMessage(absSender, chatId, message, null);
	}

	public static void sendMessage(AbsSender absSender, long chatId, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");

		SendMessage sendMessage = new SendMessage()
				.setChatId(Long.toString(chatId))
				.setText(message)
				.enableHtml(true);

		if (inlineKeyboardMarkup != null)
			sendMessage.setReplyMarkup(inlineKeyboardMarkup);

		try {
			absSender.sendMessage(sendMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}
	
	public static void editMessageText(AbsSender absSender, long chatId, int messageId, String message) {
		editMessageText(absSender, chatId, messageId, message, null);
	}

	public static void editMessageText(AbsSender absSender, long chatId, int messageId, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");

		EditMessageText editMessageText = new EditMessageText()
				.setChatId(Long.toString(chatId))
				.setMessageId(messageId)
				.setText(message)
				.enableHtml(true);

		if (inlineKeyboardMarkup != null)
			editMessageText.setReplyMarkup(inlineKeyboardMarkup);

		try {
			absSender.editMessageText(editMessageText);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

	public static void answerCallbackQuery(AbsSender absSender, String callbackQueryId) {
		answerCallbackQuery(absSender, callbackQueryId, "");
	}

	public static void answerCallbackQuery(AbsSender absSender, String callbackQueryId, String text) {
		if (absSender == null)
			throw new NullPointerException("absSender");

		AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
		answerCallbackQuery.setCallbackQueryId(callbackQueryId);
		if (StringUtil.isBlank(text) == false)
			answerCallbackQuery.setText(text);

		try {
			absSender.answerCallbackQuery(answerCallbackQuery);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

	public static void sendExceptionMessage(AbsSender absSender, long chatId, Throwable e) {
		sendMessage(absSender, chatId, String.format("요청을 처리하는 중 예외가 발생하였습니다. 관리자에게 문의하세요.\n\n예외 : %s", e.toString()));
	}

	private BotCommandUtils() {
	}

}
