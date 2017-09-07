package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import java.util.Arrays;
import java.util.List;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommand;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.RequestHandlerRegistry;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.website.WebSite;

public class WebSiteBoardSelectedRequestHandler extends AbstractBotCommandRequestHandler {

	private final WebSite site;

	private final RequestHandlerRegistry requestHandlerRegistry;

	public WebSiteBoardSelectedRequestHandler(TorrentBotResource torrentBotResource, RequestHandlerRegistry requestHandlerRegistry) {
		super("$selected$");

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

		BotCommand listBotCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(WebSiteBoardListRequestHandler.class);
		BotCommand inlineKeyboardSearchBotCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(WebSiteBoardSearchInlineKeyboardRequestHandler.class);

		// 인라인 키보드를 설정한다.
		List<InlineKeyboardButton> keyboardButtonList01 = Arrays.asList(
				new InlineKeyboardButton()
						.setText("게시판 조회")
						.setCallbackData(BotCommandUtils.toComplexBotCommandString(listBotCommand.getCommand())),
				new InlineKeyboardButton()
						.setText("게시판 검색")
						.setCallbackData(BotCommandUtils.toComplexBotCommandString(inlineKeyboardSearchBotCommand.getCommand()))
		);

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList01));

		BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), new StringBuilder().append("[ ").append(board.getDescription()).append(" ] 게시판이 선택되었습니다.").toString(), inlineKeyboardMarkup);
	}

	private WebSiteBoard findBoard(String command, String[] parameters, boolean containInitialChar) {
		if (parameters == null || parameters.length == 0) {
			if (containInitialChar == false)
				return null;

			return this.site.getBoardByCode(command);
		} else if (parameters.length == 1) {
			BotCommand botCommand = (BotCommand) this.requestHandlerRegistry.getRequestHandler(WebSiteBoardSelectRequestHandler.class);
			if (botCommand != null) {
				if (command.equals(botCommand.getCommand()) == true && containInitialChar == true) {
					return this.site.getBoardByCode(parameters[0]);
				}

				if (command.equals(botCommand.getCommandKor()) == true)
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
