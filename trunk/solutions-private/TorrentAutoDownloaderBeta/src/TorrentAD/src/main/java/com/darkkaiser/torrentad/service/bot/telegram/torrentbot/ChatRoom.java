package com.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.concurrent.atomic.AtomicLong;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import org.jsoup.helper.StringUtil;

public final class ChatRoom {

	private final long chatId;

	// 사용자가 조회, 검색등의 작업을 요청하였을 때, 각각의 작업을 구분하기 위한 ID
	private final AtomicLong requestId = new AtomicLong(0);

	// 조회 및 검색하려는 게시판
	private WebSiteBoard board;

	// 사용자가 최근에 요청하여 실행된 핸들러
	private RequestHandler latestRequestHandler;

	private final MetadataRepository metadataRepository;

	public ChatRoom(long chatId, WebSite site, MetadataRepository metadataRepository) {
		if (site == null)
			throw new NullPointerException("site");
		if (metadataRepository == null)
			throw new NullPointerException("metadataRepository");

		this.chatId = chatId;
		this.metadataRepository = metadataRepository;

		String boardCode = this.metadataRepository.getString(getProperiesKeyString(Constants.BOT_SERVICE_MR_KEY_CHAT_ID_SUBKEY_BOARD_CODE), "");
		if (StringUtil.isBlank(boardCode) == false) {
			WebSiteBoard board = site.getBoardByCode(boardCode);
			if (board != null)
				this.board = board;
		}
	}

	public final long getChatId() {
		return this.chatId;
	}

	public long getRequestId() {
		return this.requestId.get();
	}

	public long incrementAndGetRequestId() {
		return this.requestId.incrementAndGet();
	}

	public synchronized WebSiteBoard getBoard() {
		return this.board;
	}

	public synchronized void setBoard(WebSiteBoard board) {
		if (board == null)
			throw new NullPointerException("board");

		this.board = board;
		this.metadataRepository.setString(getProperiesKeyString(Constants.BOT_SERVICE_MR_KEY_CHAT_ID_SUBKEY_BOARD_CODE), this.board.getCode());
	}

	public synchronized void setLatestRequestHandler(RequestHandler requestHandler) {
		this.latestRequestHandler = requestHandler;
	}

	public synchronized RequestHandler getLatestRequestHandler() {
		return this.latestRequestHandler;
	}

	private String getProperiesKeyString(String subKey) {
		return String.format("%s-%d.%s", Constants.BOT_SERVICE_MR_KEY_CHAT_ID_PREFIX, this.chatId, subKey);
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(ChatRoom.class.getSimpleName())
				.append("{")
				.append("chatId:").append(getChatId())
				.append(", board:").append(getBoard())
				.append(", requestId:").append(getRequestId())
				.append("}")
				.toString();
	}

}