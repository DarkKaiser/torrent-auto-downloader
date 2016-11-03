package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult.Torrent;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.website.WebSite;

// @@@@@
public class GetTorrentImmediatelyTaskAction extends AbstractTorrentBotImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(GetTorrentImmediatelyTaskAction.class);
	
	private final long requestId;

	private final WebSite site;

	private final ChatRoom chatRoom;

	private final TorrentBotResource torrentBotResource;
	
	private final AbsSender absSender;

	public GetTorrentImmediatelyTaskAction(long requestId, ChatRoom chatRoom, TorrentBotResource torrentBotResource, AbsSender absSender) {
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");

		this.chatRoom = chatRoom;
		this.requestId = requestId;
		this.absSender = absSender;
		this.torrentBotResource = torrentBotResource;
		this.site = torrentBotResource.getSite();
	}

	@Override
	public String getName() {
		return "토렌트서버 상태조회";
	}

	@Override
	public Boolean call() throws Exception {
		// @@@@@
		try {
			TorrentClient torrentClient = this.torrentBotResource.getTorrentClient();
			
			TorrentGetMethodResult torrentGet = torrentClient.getTorrent();
			List<Torrent> torrents = torrentGet.arguments.torrents;
			
			StringBuilder sbAnswerMessage = new StringBuilder();
			sbAnswerMessage.append("토렌트 상태가 조회되었습니다.\n\n");

			for (Torrent torrent : torrents) {
				sbAnswerMessage.append("id:").append(torrent.getId()).append(", status:").append(torrent.status()).append("\n");
			}
			
			SendMessage answerMessage = new SendMessage()
					.setChatId(Long.toString(this.chatRoom.getChatId()))
					.setText(sbAnswerMessage.toString())
					.enableHtml(true);
			
			try {
				absSender.sendMessage(answerMessage);
			} catch (TelegramApiException e) {
				logger.error(null, e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}

		return true;
	}

	@Override
	public void validate() {
		super.validate();
		
//		if (this.torrentClient == null)
//			throw new NullPointerException("torrentClient");
	}

}
