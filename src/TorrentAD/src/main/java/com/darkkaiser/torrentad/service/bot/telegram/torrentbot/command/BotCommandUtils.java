package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command;

import com.darkkaiser.torrentad.util.OutParam;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public final class BotCommandUtils {
	
    public static void parseBotCommand(final String message, final OutParam<String> outCommand, final OutParam<String[]> outParameters, final OutParam<Boolean> outContainInitialChar) {
		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");

	    Objects.requireNonNull(outCommand, "outCommand");
	    Objects.requireNonNull(outParameters, "outParameters");

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

    public static String toComplexBotCommandString(final String... args) {
    	StringBuilder sbComplexBotCommand = new StringBuilder()
			    .append(BotCommandConstants.BOT_COMMAND_INITIAL_CHARACTER);

    	for (final String argument : args) {
    		sbComplexBotCommand.append(argument).append(BotCommandConstants.COMPLEX_BOT_COMMAND_PARAMETER_SEPARATOR);
    	}

    	return sbComplexBotCommand.delete(sbComplexBotCommand.length() - 1, sbComplexBotCommand.length()).toString();
    }

	public static void sendMessage(final AbsSender absSender, final Long chatId, final String message) {
		sendMessage(absSender, chatId, message, null, null);
	}

	public static void sendMessage(final AbsSender absSender, final Long chatId, final String message, final Integer replyToMessageId) {
		sendMessage(absSender, chatId, message, replyToMessageId, null);
	}

	public static void sendMessage(final AbsSender absSender, final Long chatId, final String message, final ReplyKeyboard replyMarkup) {
		sendMessage(absSender, chatId, message, null, replyMarkup);
	}

	public static void sendMessage(final AbsSender absSender, final Long chatId, final String message, final Integer replyToMessageId, final ReplyKeyboard replyMarkup) {
		Objects.requireNonNull(absSender, "absSender");
		Objects.requireNonNull(chatId, "chatId");

		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");

		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(Long.toString(chatId));
		sendMessage.setText(message);
		sendMessage.enableHtml(true);

		if (replyToMessageId != null)
			sendMessage.setReplyToMessageId(replyToMessageId);

		if (replyMarkup != null)
			sendMessage.setReplyMarkup(replyMarkup);

		try {
			absSender.execute(sendMessage);
		} catch (final TelegramApiException e) {
			log.error(null, e);
		}
	}
	
	public static void editMessageText(final AbsSender absSender, final Long chatId, final Integer messageId, final String message) {
		editMessageText(absSender, chatId, messageId, message, null);
	}

	public static void editMessageText(final AbsSender absSender, final Long chatId, final Integer messageId, final String message, final InlineKeyboardMarkup inlineKeyboardMarkup) {
		Objects.requireNonNull(absSender, "absSender");
		Objects.requireNonNull(chatId, "chatId");
		Objects.requireNonNull(messageId, "messageId");

		if (StringUtil.isBlank(message) == true)
			throw new IllegalArgumentException("message는 빈 문자열을 허용하지 않습니다.");

		EditMessageText editMessageText = new EditMessageText();
		editMessageText.setChatId(Long.toString(chatId));
		editMessageText.setMessageId(messageId);
		editMessageText.setText(message);
		editMessageText.enableHtml(true);

		if (inlineKeyboardMarkup != null)
			editMessageText.setReplyMarkup(inlineKeyboardMarkup);

		try {
			absSender.execute(editMessageText);
		} catch (final TelegramApiException e) {
			log.error(null, e);
		}
	}

	public static void answerCallbackQuery(final AbsSender absSender, final String callbackQueryId) {
		answerCallbackQuery(absSender, callbackQueryId, "");
	}

	public static void answerCallbackQuery(final AbsSender absSender, final String callbackQueryId, final String text) {
		Objects.requireNonNull(absSender, "absSender");

		AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
		answerCallbackQuery.setCallbackQueryId(callbackQueryId);
		if (StringUtil.isBlank(text) == false)
			answerCallbackQuery.setText(text);

		try {
			absSender.execute(answerCallbackQuery);
		} catch (final TelegramApiException e) {
			log.error(null, e);
		}
	}

	public static void sendExceptionMessage(final AbsSender absSender, final Long chatId, final Throwable e) {
		sendMessage(absSender, chatId, String.format("요청을 처리하는 중 예외가 발생하였습니다. 관리자에게 문의하세요.\n\n예외 : %s", e.toString()));
	}

	private BotCommandUtils() {

	}

}
