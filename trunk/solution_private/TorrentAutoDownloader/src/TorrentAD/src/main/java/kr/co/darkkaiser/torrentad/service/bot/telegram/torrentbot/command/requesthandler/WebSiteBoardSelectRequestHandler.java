package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class WebSiteBoardSelectRequestHandler extends AbstractBotCommandRequestHandler {

	private final WebSite site;

	public WebSiteBoardSelectRequestHandler(TorrentBotResource torrentBotResource) {
		super("선택", "조회 및 검색하려는 게시판을 선택합니다.");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");

		this.site = torrentBotResource.getSite();
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 0, 0) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar) {
		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("조회 및 검색하려는 게시판을 선택하세요:\n\n");

		WebSiteBoard[] boardValues = this.site.getBoardValues();
		for (int index = 0; index < boardValues.length; ++index) {
			WebSiteBoard board = boardValues[index];

			sbAnswerMessage.append(index + 1).append(". ")
					.append(board.getDescription())
					.append(" : ")
					.append("/").append(board.getCode()).append("\n");
		}
		
		sendAnswerMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardSelectRequestHandler.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
