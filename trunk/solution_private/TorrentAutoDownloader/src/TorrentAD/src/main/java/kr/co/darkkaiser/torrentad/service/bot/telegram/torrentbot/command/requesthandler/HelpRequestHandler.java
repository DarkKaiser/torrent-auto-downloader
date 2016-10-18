package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;

public class HelpRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(HelpRequestHandler.class);

	private final RequestHandlerRegistry requestHandlerRegistry;

	public HelpRequestHandler(RequestHandlerRegistry requestHandlerRegistry) {
		super("도움", "도움말을 표시합니다.");

		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		StringBuilder sbMessageText = new StringBuilder();
		sbMessageText.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

		for (RequestHandler handler : this.requestHandlerRegistry.getRequestHandlers()) {
			if (handler instanceof BotCommand) {
				BotCommand command = (BotCommand) handler;
				sbMessageText.append("<b>").append(command.getCommand()).append("</b>\n")
						.append(command.getCommandDescription()).append("\n\n");
			}
		}

		SendMessage helpMessage = new SendMessage()
				.setChatId(chat.getId().toString())
				.setText(sbMessageText.toString())
				.enableHtml(true);

		try {
			absSender.sendMessage(helpMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(HelpRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
