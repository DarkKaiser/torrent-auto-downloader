package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult.Torrent;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;

public class TorrentStatusImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(TorrentStatusImmediatelyTaskAction.class);
	
	private final AbsSender absSender;

	private final ChatRoom chatRoom;

	private final TorrentBotResource torrentBotResource;

	public TorrentStatusImmediatelyTaskAction(AbsSender absSender, ChatRoom chatRoom, TorrentBotResource torrentBotResource) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");

		this.chatRoom = chatRoom;
		this.absSender = absSender;
		this.torrentBotResource = torrentBotResource;
	}

	@Override
	public String getName() {
		return "토렌트서버 상태 조회";
	}

	@Override
	public Boolean call() throws Exception {
		try {
			/////////////////////////////////////////////////////
			// @@@@@
			TorrentClient torrentClient = this.torrentBotResource.getTorrentClient();
			/////////////////////////////////////////////////////
			
			TorrentGetMethodResult methodResult = torrentClient.getTorrent();
			
			if (methodResult == null) {
				BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), "토렌트 서버의 상태 조회가 실패하였습니다.\n문제가 지속적으로 발생하는 경우에는 관리자에게 문의하세요.");	
			} else {
				List<Torrent> torrents = methodResult.arguments.torrents;

				StringBuilder sbAnswerMessage = new StringBuilder();
				sbAnswerMessage.append("토렌트 서버의 상태 조회가 완료되었습니다.\n\n");

				if (torrents.size() == 0) {
					sbAnswerMessage.append("등록된 토렌트 파일이 없습니다.");
				} else {
					int index = 0;
					for (Torrent torrent : torrents) {
						sbAnswerMessage.append(++index).append(". ").append(torrent.getName()).append("(").append(torrent.getStatusString()).append(", ").append((int) (torrent.getPercentDone() * 100)).append("%)\n\n");
					}
				}

				BotCommandUtils.sendMessage(this.absSender, this.chatRoom.getChatId(), sbAnswerMessage.toString());			
			}
		} catch (Exception e) {
			logger.error(null, e);

			BotCommandUtils.sendExceptionMessage(absSender, this.chatRoom.getChatId(), e);

			return false;
		}

		return true;
	}

	@Override
	public void validate() {
		super.validate();

		if (this.absSender == null)
			throw new NullPointerException("absSender");
		if (this.chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (this.torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
	}

}
