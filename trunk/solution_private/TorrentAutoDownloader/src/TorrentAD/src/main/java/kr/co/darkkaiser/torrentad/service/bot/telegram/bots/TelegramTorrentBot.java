package kr.co.darkkaiser.torrentad.service.bot.telegram.bots;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;

import kr.co.darkkaiser.torrentad.service.bot.telegram.commands.HelpCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.commands.StartCommand;

public class TelegramTorrentBot extends TelegramLongPollingCommandBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramTorrentBot.class);
	
	// @@@@@
	private final ConcurrentHashMap<Integer, Integer> userState = new ConcurrentHashMap<>();
	
	public TelegramTorrentBot() {
        register(new StartCommand());
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);

        // @@@@@
//        int state = userState.getOrDefault(message.getFrom().getId(), 0);
//        userState.put(message.getFrom().getId(), WAITINGCHANNEL);
//        userState.remove(message.getFrom().getId());

		registerDefaultAction((absSender, message) -> {
			SendMessage commandUnknownMessage = new SendMessage();
			commandUnknownMessage.setChatId(message.getChatId().toString());
			commandUnknownMessage.setText("'" + message.getText() + "'는 등록되지 않은 명령어입니다. 아래 도움말을 참고하세요.");

			try {
				absSender.sendMessage(commandUnknownMessage);
			} catch (TelegramApiException e) {
				logger.error(null, e);
			}

			helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});
		});
	}

	@Override
	public void processNonCommandUpdate(Update update) {
		if (update.hasMessage() == true) {
            Message message = update.getMessage();
            if (message.hasText() == true) {
                SendMessage nonCommandMessage = new SendMessage();
                nonCommandMessage.setChatId(message.getChatId().toString());
                nonCommandMessage.setText("'" + message.getText() + "'는 등록되지 않은 명령어입니다. 명령어를 모르시면 '/help' 를 입력하세요." );

                try {
                    sendMessage(nonCommandMessage);
                } catch (TelegramApiException e) {
                	logger.error(null, e);
                }
            }
        }
	}
	
	@Override
	public String getBotUsername() {
		return "darkkaiser_torrentad_bot";
	}

	@Override
	public String getBotToken() {
		return "298010919:AAEcnGlwklr6PXMQ4UUB7JbgMN3zTqrr6r4";
	}

}
