package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import com.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.TorrentStatusImmediatelyTaskAction;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Objects;

@Slf4j
public class TorrentStatusResultCallbackQueryRequestHandler extends AbstractBotCommandRequestHandler {

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public TorrentStatusResultCallbackQueryRequestHandler(final TorrentBotResource torrentBotResource, final ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.TSSR_RESULT_CALLBACK_QUERY_COMMAND);

		Objects.requireNonNull(torrentBotResource, "torrentBotResource");
		Objects.requireNonNull(immediatelyTaskExecutorService, "immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(final String command, final String[] parameters, final boolean containInitialChar) {
		if (super.executable0(command, parameters, containInitialChar, 1, 1) == false)
			return false;

		String callbackQueryCommand = parameters[0];
		return callbackQueryCommand.equals(BotCommandConstants.TSSR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA) != false
				|| callbackQueryCommand.equals(BotCommandConstants.TSSR_REFRESH_ETC_INLINE_KEYBOARD_BUTTON_DATA) != false;
	}

	@Override
	public void execute(final AbsSender absSender, final ChatRoom chatRoom, final Update update, final String command, final String[] parameters, final boolean containInitialChar) {
		try {
			String callbackQueryCommand = parameters[0];
			String callbackQueryId = update.getCallbackQuery().getId();
			Integer callbackQueryMessageId = update.getCallbackQuery().getMessage().getMessageId();

			BotCommandUtils.answerCallbackQuery(absSender, callbackQueryId);
			
			//
			// 새로고침 인라인명령(1)
			//
			if (callbackQueryCommand.equals(BotCommandConstants.TSSR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				BotCommandUtils.editMessageText(absSender, chatRoom.getChatId(), callbackQueryMessageId, "토렌트 서버의 상태를 조회중입니다...");

				// 토렌트 서버의 상태 조회를 시작한다.
				this.immediatelyTaskExecutorService.submit(
						new TorrentStatusImmediatelyTaskAction(callbackQueryMessageId, absSender, chatRoom, this.torrentBotResource));
			//
			// 새로고침 인라인명령(2)
			//
			} else if (callbackQueryCommand.equals(BotCommandConstants.TSSR_REFRESH_ETC_INLINE_KEYBOARD_BUTTON_DATA) == true) {
				BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "토렌트 서버의 상태를 조회중입니다...");

				// 토렌트 서버의 상태 조회를 시작한다.
				this.immediatelyTaskExecutorService.submit(
						new TorrentStatusImmediatelyTaskAction(absSender, chatRoom, this.torrentBotResource));
			} else {
				throw new IllegalArgumentException(String.format("지원하지 않는 인라인 명령(%s)입니다.", callbackQueryCommand));
			}
		} catch (final Exception e) {
			log.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, chatRoom.getChatId(), e);
		}
	}

	@Override
	public String toString() {
		return TorrentStatusResultCallbackQueryRequestHandler.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
