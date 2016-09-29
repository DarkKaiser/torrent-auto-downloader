package kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.commands.CommandRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.commands.HelpCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegrambot.telegramtorrentbot.commands.ListCommand;

public class TelegramTorrentBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramTorrentBot.class);
	
	private final CommandRegistry commandRegistry;
	
	// @@@@@
	private final ConcurrentHashMap<Integer/* CHAT_ID or user id */, User> userState = new ConcurrentHashMap<>();
	
	private TorrentJob job;
	// job.search(chat_id)
	// job.get(chat_id)
	// job.list(chat_id)

	public TelegramTorrentBot() {
		// 부모클래스의 멤버변수 액세스
//		http://stackoverflow.com/questions/3603405/access-a-private-variable-of-the-super-class-in-java-jchart2d
//		 try {
//			Field field = TelegramLongPollingBot.class.getDeclaredField("exe");
//			field.setAccessible(true);
//	        Object value = field.get(this);
//	        field.setAccessible(false);
//
//	        if (value == null) {
//	            return null;
//	        } else if (Rectangle2D.class.isAssignableFrom(value.getClass())) {
//	            return (Rectangle2D) value;
//	        }
//	        throw new RuntimeException("Wrong value");
//		} catch (NoSuchFieldException | SecurityException e) {
//			e.printStackTrace();
//		}
		 
		this.commandRegistry = new CommandRegistry();
		
		this.commandRegistry.register(new ListCommand());
		
        HelpCommand helpCommand = new HelpCommand(this.commandRegistry);
        this.commandRegistry.register(helpCommand);

//        // @@@@@
////        int state = userState.getOrDefault(message.getFrom().getId(), 0);
////        userState.put(message.getFrom().getId(), WAITINGCHANNEL);
////        userState.remove(message.getFrom().getId());
//
//		registerDefaultAction((absSender, message) -> {
//			SendMessage commandUnknownMessage = new SendMessage();
//			commandUnknownMessage.setChatId(message.getChatId().toString());
//			commandUnknownMessage.setText("'" + message.getText() + "'는 등록되지 않은 명령어입니다. 아래 도움말을 참고하세요.");
//
//			try {
//				absSender.sendMessage(commandUnknownMessage);
//			} catch (TelegramApiException e) {
//				logger.error(null, e);
//			}
//
//			helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});
//		});
	}

//	@Override
//	public void processNonCommandUpdate(Update update) {
//		if (update.hasMessage() == true) {
//            Message message = update.getMessage();
//            if (message.hasText() == true) {
//                SendMessage nonCommandMessage = new SendMessage();
//                nonCommandMessage.setChatId(message.getChatId().toString());
//                nonCommandMessage.setText("'" + message.getText() + "'는 등록되지 않은 명령어입니다. 명령어를 모르시면 '/help' 를 입력하세요." );
//
//                try {
//                    sendMessage(nonCommandMessage);
//                } catch (TelegramApiException e) {
//                	logger.error(null, e);
//                }
//            }
//        }
//	}
	
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
		// @@@@@
		if (update.hasMessage() == true) {
            Message message = update.getMessage();
//            if (message.isCommand()) {
                if (this.commandRegistry.executeCommand(this, message)) {
                    return;
                }
                
                // 검색어나 기타 다른것인지 확인
                // this.commandRegistry.executeDefaultCommand()
                
//            }
        }

		// ?????
//        processNonCommandUpdate(update);
	}

}
