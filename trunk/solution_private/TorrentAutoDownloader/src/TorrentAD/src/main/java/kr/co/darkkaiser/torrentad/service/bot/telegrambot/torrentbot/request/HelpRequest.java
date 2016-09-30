package kr.co.darkkaiser.torrentad.service.bot.telegrambot.torrentbot.request;

// @@@@@
public class HelpRequest extends AbstractRequest {

	public HelpRequest() {
		super("도움", "도움말을 표시합니다.");
	}
	
	public boolean userShowRequest() {
		return true;
	}
	
}
