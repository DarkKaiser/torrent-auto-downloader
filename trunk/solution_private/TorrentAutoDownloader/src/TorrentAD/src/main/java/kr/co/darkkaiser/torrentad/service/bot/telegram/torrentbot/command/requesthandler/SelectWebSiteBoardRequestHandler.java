package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class SelectWebSiteBoardRequestHandler extends AbstractBotCommandRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(SelectWebSiteBoardRequestHandler.class);
	
	private final WebSite site;

	public SelectWebSiteBoardRequestHandler(WebSite site) {
		super("선택", "조회 및 검색하려는 게시판을 선택합니다.");

		if (site == null)
			throw new NullPointerException("site");

		this.site = site;
	}

	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, false) == false)
			return false;

		return true;
	}

	@Override
	public void execute(AbsSender absSender, User user, Chat chat, String command, String[] parameters, boolean containInitialChar) {
		StringBuilder sbMessageText = new StringBuilder();
		sbMessageText.append("조회 및 검색하려는 게시판을 선택하세요:\n\n");

		WebSiteBoard[] boardValues = this.site.getBoardValues();
		for (int index = 0; index < boardValues.length; ++index) {
			WebSiteBoard board = boardValues[index];

			sbMessageText.append(index + 1).append(". ")
					.append(board.getDescription())
					.append(" : ")
					.append("/").append(board.getCode()).append("\n");
		}

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

	@Override
	public String toString() {
		return new StringBuilder()
				.append(SelectWebSiteBoardRequestHandler.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
