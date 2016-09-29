package kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.command.CommandRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.command.HelpCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.command.ListCommand;

public class TelegramTorrentBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramTorrentBot.class);
	
	private final CommandRegistry commandRegistry;
	
	// @@@@@
	private final ConcurrentHashMap<Integer/* CHAT_ID or user id */, User> userState = new ConcurrentHashMap<>();
	
	// @@@@@
	private TorrentJob job;

	public TelegramTorrentBot() {
		this.commandRegistry = new CommandRegistry();

		this.commandRegistry.register(new ListCommand());
        this.commandRegistry.register(new HelpCommand(this.commandRegistry));

//        // @@@@@
//        int state = userState.getOrDefault(message.getFrom().getId(), 0);
//        userState.put(message.getFrom().getId(), WAITINGCHANNEL);
//        userState.remove(message.getFrom().getId());
	}

	@Override
	public String getBotUsername() {
		return "darkkaiser_torrentad_bot";
	}

	@Override
	public String getBotToken() {
		return "298010919:AAEcnGlwklr6PXMQ4UUB7JbgMN3zTqrr6r4";
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update == null)
			throw new NullPointerException("update");

		try {
			if (update.hasMessage() == true) {
	            Message message = update.getMessage();
	            if (this.commandRegistry.executeCommand(this, message))
	                return;

	            // 검색어나 기타 다른것인지 확인
	            // @@@@@
	        }

			onCommandUnknownMessage(update);
		} catch (Exception e) {
			logger.error(null, e);
		}
	}

	private void onCommandUnknownMessage(Update update) {
		if (update == null)
			throw new NullPointerException("update");

		if (update.hasMessage() == true) {
			Message message = update.getMessage();

			StringBuilder sbMessage = new StringBuilder();
			if (message.hasText() == true)
				sbMessage.append("'").append(message.getText()).append("'는 등록되지 않은 명령어입니다.\n");

			sbMessage.append("명령어를 모르시면 '도움'을 입력하세요.");

			SendMessage commandUnknownMessage = new SendMessage();
			commandUnknownMessage.setChatId(message.getChatId().toString());
			commandUnknownMessage.setText(sbMessage.toString());

			try {
				sendMessage(commandUnknownMessage);
			} catch (TelegramApiException e) {
				logger.error(null, e);
			}
		}
	}

}
