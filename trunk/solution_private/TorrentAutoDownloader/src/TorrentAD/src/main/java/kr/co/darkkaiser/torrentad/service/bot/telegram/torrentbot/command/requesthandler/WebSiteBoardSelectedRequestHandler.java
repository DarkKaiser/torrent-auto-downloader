package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardSelectedRequestHandler extends AbstractRequestHandler {

	private final WebSite site;

	private final RequestHandlerRegistry requestHandlerRegistry;

	public WebSiteBoardSelectedRequestHandler(WebSite site, RequestHandlerRegistry requestHandlerRegistry) {
		super("선택완료");

		if (site == null)
			throw new NullPointerException("site");
		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.site = site;
		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (findBoard(command, parameters, containInitialChar) == null)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar) {
		WebSiteBoard board = findBoard(command, parameters, containInitialChar);
		if (board == null) {
			// @@@@@ 선택되지 않으면??			
		}

		chatRoom.setBoard(board);

		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("[ ").append(board.getDescription()).append(" ] 게시판이 선택되었습니다.");

		sendAnswerMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
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
			BotCommand botCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(WebSiteBoardSelectRequestHandler.class);
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
				.append(WebSiteBoardSelectedRequestHandler.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
