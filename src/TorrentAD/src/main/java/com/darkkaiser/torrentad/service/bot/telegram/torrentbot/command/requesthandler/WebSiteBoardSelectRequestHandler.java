package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.ExposedBotCommand;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Objects;

public class WebSiteBoardSelectRequestHandler extends AbstractBotCommandRequestHandler implements ExposedBotCommand {

	private final WebSite site;

	public WebSiteBoardSelectRequestHandler(final TorrentBotResource torrentBotResource) {
		super("select", "선택", "/select (선택)", "조회 및 검색하려는 게시판을 선택합니다.");

		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(torrentBotResource.getSite(), "site");

		this.site = torrentBotResource.getSite();
	}

	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
        return super.executable0(command, parameters, containInitialChar, 0, 0) != false;
    }

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		StringBuilder sbAnswerMessage = new StringBuilder();
		sbAnswerMessage.append("조회 및 검색하려는 게시판을 선택하세요:\n\n");

		WebSiteBoard[] boardValues = this.site.getBoardValues();
        if (boardValues != null) {
            for (int index = 0; index < boardValues.length; ++index) {
                WebSiteBoard board = boardValues[index];

                sbAnswerMessage.append(String.format("%02d", index + 1)).append(". ")
                        .append(board.getDescription())
                        .append(" : ")
                        .append("/").append(board.getCode()).append("\n");
            }

			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
        }
	}

	@Override
	public String toString() {
		return WebSiteBoardSelectRequestHandler.class.getSimpleName() +
				"{" +
				"site:" + this.site +
				"}, " +
				super.toString();
	}

}
