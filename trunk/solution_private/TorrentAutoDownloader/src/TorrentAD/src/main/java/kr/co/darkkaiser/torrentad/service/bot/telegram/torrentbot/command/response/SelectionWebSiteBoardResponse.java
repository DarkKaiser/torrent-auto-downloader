package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;

// @@@@@ response는 없어도 되지 않나??
public class SelectionWebSiteBoardResponse extends AbstractResponse {
	
	private ChatRoom chat;

	public SelectionWebSiteBoardResponse(ChatRoom chat) {
		super("id");
		
		this.chat = chat;
	}

}
