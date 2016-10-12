package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public class SelectBoardRequest extends AbstractBotCommandRequest {

	private static final Logger logger = LoggerFactory.getLogger(SelectBoardRequest.class);

	public SelectBoardRequest() {
		super("select", "선택", "게시판을 선택합니다.");
	}

	// @@@@@
	@Override
	public Response execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
		StringBuilder sbMessage = new StringBuilder();
		sbMessage.append("입력 가능한 명령어는 아래와 같습니다:\n\n");

//		for (Request request : this.requestResponseRegistry.getRegisteredRequests()) {
//			if (request instanceof BotCommand) {
//				BotCommand command = (BotCommand) request;
//				sbMessage.append("<b>").append(command.getCommand()).append("</b>\n").append(command.getCommandDescription()).append("\n\n");
//			}
//		}
//
//		SendMessage helpMessage = new SendMessage();
//		helpMessage.setChatId(chat.getId().toString());
//		helpMessage.setText(sbMessage.toString());
//		helpMessage.enableHtml(true);
//
//		try {
//			absSender.sendMessage(helpMessage);
//		} catch (TelegramApiException e) {
//			logger.error(null, e);
//		}

		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectBoardRequest.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
