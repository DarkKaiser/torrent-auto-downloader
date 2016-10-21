package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class SelectedWebSiteBoardRequestHandler extends AbstractRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(SelectedWebSiteBoardRequestHandler.class);

	private final WebSite site;

	// @@@@@
	private final ChatRoom chat;

	private final RequestHandlerRegistry requestHandlerRegistry;

	public SelectedWebSiteBoardRequestHandler(WebSite site, ChatRoom chat, RequestHandlerRegistry requestHandlerRegistry) {
		super("선택완료");

		if (site == null)
			throw new NullPointerException("site");
		if (chat == null)// @@@@@
			throw new NullPointerException("chat");
		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.site = site;
		this.chat = chat;//@@@@@
		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (findBoard(command, parameters, containInitialChar) == null)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String command, String[] parameters, boolean containInitialChar) {
		WebSiteBoard board = findBoard(command, parameters, containInitialChar);

		// @@@@@
		this.chat.setBoard(board);

		StringBuilder sbMessageText = new StringBuilder();
		sbMessageText.append("[ ").append(board.getDescription()).append(" ] 게시판이 선택되었습니다.");

		SendMessage answerMessage = new SendMessage()
				.setChatId(chat.getId().toString())
				.setText(sbMessageText.toString())
				.enableHtml(true);
		
		try {
			absSender.sendMessage(answerMessage);
		} catch (TelegramApiException e) {
			logger.error(null, e);
		}
	}

	private WebSiteBoard findBoard(String command, String[] parameters, boolean containInitialChar) {
		WebSiteBoard[] boardValues = this.site.getBoardValues();

		if (parameters == null || parameters.length == 0) {
			if (containInitialChar == false)
				return null;

			for (WebSiteBoard board : boardValues) {
				if (board.getCode().equals(command) == true)
					return board;
			}
		} else if (parameters.length == 1) {
			BotCommand botCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(SelectWebSiteBoardRequestHandler.class);
			if (botCommand != null && botCommand.getCommand().equals(command) == true) {
				String parameter = parameters[0];
				for (WebSiteBoard board : boardValues) {
					if (board.getCode().equals(parameter) == true)
						return board;
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectedWebSiteBoardRequestHandler.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
