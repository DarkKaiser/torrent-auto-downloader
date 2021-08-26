package com.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.RequestHandler;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import lombok.Getter;
import org.jsoup.helper.StringUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class ChatRoom {

	@Getter
	private final long chatId;

	// 사용자가 조회, 검색등의 작업을 요청하였을 때, 각각의 작업을 구분하기 위한 ID
	private final AtomicLong requestId = new AtomicLong(0);

	// 조회 및 검색하려는 게시판
	private WebSiteBoard board;

	// 사용자가 최근에 요청하여 실행된 핸들러
	private RequestHandler latestRequestHandler;

	private final MetadataRepository metadataRepository;

	public ChatRoom(final long chatId, final WebSite site, final MetadataRepository metadataRepository) {
		Objects.requireNonNull(site, "site");
		Objects.requireNonNull(metadataRepository, "metadataRepository");

		this.chatId = chatId;
		this.metadataRepository = metadataRepository;

		String boardCode = this.metadataRepository.getString(getProperiesKeyString(Constants.BOT_SERVICE_MR_KEY_CHAT_ID_SUBKEY_BOARD_CODE), "");
		if (StringUtil.isBlank(boardCode) == false) {
			WebSiteBoard board = site.getBoardByCode(boardCode);
			if (board != null)
				this.board = board;
		}
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

	public synchronized void setBoard(final WebSiteBoard board) {
        Objects.requireNonNull(board, "board");

		this.board = board;
		this.metadataRepository.setString(getProperiesKeyString(Constants.BOT_SERVICE_MR_KEY_CHAT_ID_SUBKEY_BOARD_CODE), this.board.getCode());
	}

	public synchronized void setLatestRequestHandler(final RequestHandler requestHandler) {
		this.latestRequestHandler = requestHandler;
	}

	public synchronized RequestHandler getLatestRequestHandler() {
		return this.latestRequestHandler;
	}

	private String getProperiesKeyString(final String subKey) {
		return String.format("%s-%d.%s", Constants.BOT_SERVICE_MR_KEY_CHAT_ID_PREFIX, this.chatId, subKey);
	}
	
	@Override
	public String toString() {
		return ChatRoom.class.getSimpleName() +
				"{" +
				"chatId:" + getChatId() +
				", board:" + getBoard() +
				", requestId:" + getRequestId() +
				"}";
	}

}
