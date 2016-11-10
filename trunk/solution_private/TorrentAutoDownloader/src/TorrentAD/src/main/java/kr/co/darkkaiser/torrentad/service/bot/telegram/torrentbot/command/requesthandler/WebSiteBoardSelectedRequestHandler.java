package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardSelectedRequestHandler extends AbstractRequestHandler {

	private final WebSite site;

	private final RequestHandlerRegistry requestHandlerRegistry;

	public WebSiteBoardSelectedRequestHandler(TorrentBotResource torrentBotResource, RequestHandlerRegistry requestHandlerRegistry) {
		super("선택완료");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");
		if (requestHandlerRegistry == null)
			throw new NullPointerException("requestHandlerRegistry");

		this.site = torrentBotResource.getSite();
		this.requestHandlerRegistry = requestHandlerRegistry;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (findBoard(command, parameters, containInitialChar) == null)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		WebSiteBoard board = findBoard(command, parameters, containInitialChar);
		if (board == null) {
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택하신 게시판을 찾을 수 없습니다. 관리자에게 문의하세요.");

			logError("입력된 명령에 해당하는 게시판을 찾을 수 없습니다.", command, parameters, containInitialChar);
			
			return;
		}

		chatRoom.setBoard(board);

		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), new StringBuilder().append("[ ").append(board.getDescription()).append(" ] 게시판이 선택되었습니다.").toString());
	}

	private WebSiteBoard findBoard(String command, String[] parameters, boolean containInitialChar) {
		if (parameters == null || parameters.length == 0) {
			if (containInitialChar == false)
				return null;

			return this.site.getBoardByCode(command);
		} else if (parameters.length == 1) {
			BotCommand botCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(WebSiteBoardSelectRequestHandler.class);
			if (botCommand != null && botCommand.getCommand().equals(command) == true) {
				return this.site.getBoardByCode(parameters[0]);
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