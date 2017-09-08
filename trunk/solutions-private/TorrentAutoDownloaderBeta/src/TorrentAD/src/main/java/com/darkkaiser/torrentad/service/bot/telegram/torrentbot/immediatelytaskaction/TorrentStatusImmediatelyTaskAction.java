package com.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Arrays;
import java.util.List;

import com.darkkaiser.torrentad.net.torrent.TorrentClient;
import com.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;

import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

public class TorrentStatusImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(TorrentStatusImmediatelyTaskAction.class);
	
	private final int messageId;
	
	private final AbsSender absSender;

	private final ChatRoom chatRoom;

	private final TorrentBotResource torrentBotResource;

	public TorrentStatusImmediatelyTaskAction(final AbsSender absSender, final ChatRoom chatRoom, final TorrentBotResource torrentBotResource) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");

		this.messageId = BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID;
		this.absSender = absSender;
		this.chatRoom = chatRoom;
		
		this.torrentBotResource = torrentBotResource;
	}
	
	public TorrentStatusImmediatelyTaskAction(final int messageId, final AbsSender absSender, final ChatRoom chatRoom, final TorrentBotResource torrentBotResource) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");

		this.messageId = messageId;
		this.absSender = absSender;
		this.chatRoom = chatRoom;
		
		this.torrentBotResource = torrentBotResource;
	}

	@Override
	public String getName() {
		return "토렌트서버 상태 조회";
	}

	@Override
	public Boolean call() throws Exception {
		try {
			TorrentClient torrentClient = this.torrentBotResource.getTorrentClient();

			// 토렌트 서버의 상태를 조회한다.
			TorrentGetMethodResult methodResult = torrentClient.getTorrent();
			if (methodResult == null) {
				torrentClient.disconnect();
				torrentClient = this.torrentBotResource.getTorrentClient();				
				methodResult = torrentClient.getTorrent();
			}

			if (methodResult == null) {
				BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "토렌트 서버의 상태 조회가 실패하였습니다.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.");
			} else {
				List<TorrentGetMethodResult.Torrent> torrents = methodResult.arguments.torrents;

				StringBuilder sbAnswerMessage = new StringBuilder();
				sbAnswerMessage.append("토렌트 서버의 상태 조회가 완료되었습니다:\n\n");

				if (torrents.size() == 0) {
					sbAnswerMessage.append("등록된 토렌트 파일이 없습니다.");
				} else {
					for (TorrentGetMethodResult.Torrent torrent : torrents) {
						sbAnswerMessage.append("☞ ").append(torrent.getName()).append(" (").append((int) (torrent.getPercentDone() * 100)).append("%, ").append(torrent.getStatusString()).append(")\n\n");
					}
				}

				// 인라인 키보드를 설정한다.
				//noinspection ArraysAsListWithZeroOrOneArgument
				List<InlineKeyboardButton> keyboardButtonList01 = Arrays.asList(
						new InlineKeyboardButton()
								.setText(BotCommandConstants.TSSR_REFRESH_INLINE_KEYBOARD_BUTTON_TEXT)
								.setCallbackData(BotCommandUtils.toComplexBotCommandString(BotCommandConstants.TSSR_RESULT_CALLBACK_QUERY_COMMAND, BotCommandConstants.TSSR_REFRESH_INLINE_KEYBOARD_BUTTON_DATA))
				);

				//noinspection ArraysAsListWithZeroOrOneArgument
				InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().setKeyboard(Arrays.asList(keyboardButtonList01));

				// 클라이언트로 토렌트 서버의 상태 메시지를 전송한다.
				if (this.messageId == BotCommandConstants.INVALID_BOT_COMMAND_MESSAGE_ID) {
					BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString(), inlineKeyboardMarkup);
				} else {
					BotCommandUtils.editMessageText(this.absSender, this.chatRoom.getChatId(), this.messageId, sbAnswerMessage.toString(), inlineKeyboardMarkup);
				}
			}
		} catch (final Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, this.chatRoom.getChatId(), e);

			return false;
		}

		return true;
	}

	@Override
	public void validate() {
		super.validate();
	}

}
