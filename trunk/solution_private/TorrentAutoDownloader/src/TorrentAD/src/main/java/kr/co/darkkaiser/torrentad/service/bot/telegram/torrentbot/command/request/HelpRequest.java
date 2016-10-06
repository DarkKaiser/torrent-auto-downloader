package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestResponseRegistry;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public class HelpRequest extends AbstractBotCommandRequest {

	private static final Logger logger = LoggerFactory.getLogger(HelpRequest.class);

	private final RequestResponseRegistry requestResponseRegistry;

	public HelpRequest(RequestResponseRegistry requestResponseRegistry) {
		super("help", "도움", "도움말을 표시합니다.");

		if (requestResponseRegistry == null)
			throw new NullPointerException("requestResponseRegistry");

		this.requestResponseRegistry = requestResponseRegistry;
	}
	
	// @@@@@
	@Override
	public Response execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		StringBuilder sbMessage = new StringBuilder();
		sbMessage.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

		for (Request request : this.requestResponseRegistry.getRegisteredRequests()) {
			if (request instanceof BotCommand) {
				BotCommand command = (BotCommand) request;
				sbMessage.append("<b>").append(command.getCommand()).append("</b>\n").append(command.getCommandDescription()).append("\n\n");
			}
		}

		SendMessage helpMessage = new SendMessage();
		helpMessage.setChatId(chat.getId().toString());
		helpMessage.setText(sbMessage.toString());
		helpMessage.enableHtml(true);

		try {
			absSender.sendMessage(helpMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}

		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(HelpRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
