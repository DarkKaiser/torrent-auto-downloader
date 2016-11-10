package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.ChatRoom;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.TorrentBotResource;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItem;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItemDownloadLink;

public class WebSiteBoardDownloadLinkListImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSiteBoardDownloadLinkListImmediatelyTaskAction.class);

	private final AbsSender absSender;

	private final ChatRoom chatRoom;

	private final WebSiteBoard board;
	
	private final long identifierValue;

	private final TorrentBotResource torrentBotResource;
	
	private final WebSite site;

	public WebSiteBoardDownloadLinkListImmediatelyTaskAction(AbsSender absSender, ChatRoom chatRoom, WebSiteBoard board, long identifierValue, TorrentBotResource torrentBotResource) {
		if (absSender == null)
			throw new NullPointerException("absSender");
		if (chatRoom == null)
			throw new NullPointerException("chatRoom");
		if (board == null)
			throw new NullPointerException("board");
		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (torrentBotResource.getSite() == null)
			throw new NullPointerException("site");

		this.board = board;
		this.chatRoom = chatRoom;
		this.identifierValue = identifierValue;
		this.absSender = absSender;
		this.torrentBotResource = torrentBotResource;
		this.site = torrentBotResource.getSite();
	}

	@Override
	public String getName() {
		return String.format("%s > %s 다운로드 링크 조회", this.site.getName(), this.board.getDescription());
	}

	@Override
	public Boolean call() throws Exception {
		try {
			////////////////////////////////////////////////////////////////
			// @@@@@
			// connector 로그인은 누가 할것인가?
			this.torrentBotResource.getSiteConnector().login();
			WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
			////////////////////////////////////////////////////////////////

			// 선택된 게시판을 조회한다.
			Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemIdentifierDescCompare());
			while (iterator.hasNext() == true) {
				// 사용자가 선택한 게시물을 찾는다.
				WebSiteBoardItem boardItem = iterator.next();
				if (boardItem.getIdentifier() != this.identifierValue)
					continue;

				// @@@@@
				// 다운로드
				BogoBogoBoardItem bogobogoBoardItem = (BogoBogoBoardItem) boardItem;
				Iterator<BogoBogoBoardItemDownloadLink> downloadLinkIterator = bogobogoBoardItem.downloadLinkIterator();
				if (downloadLinkIterator.hasNext() == false) {
					handler.download3(bogobogoBoardItem);
				}
				
				downloadLinkIterator = bogobogoBoardItem.downloadLinkIterator();
				if (downloadLinkIterator.hasNext() == false) {
					BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "다운로드 할 첨부파일을 읽을 수 업습니다.");
					return true;
				}
				
				StringBuilder sbAnswerMessage = new StringBuilder();
				sbAnswerMessage.append("어떤 파일에 대한 첨부파일 조회가 완료되었습니다.\n\n");

				int i = 0;
				while (downloadLinkIterator.hasNext() == true) {
					BogoBogoBoardItemDownloadLink next = downloadLinkIterator.next();

					++i;
					sbAnswerMessage.append(next.getFileName()).append(", 확장자:").append(next.getValue4()).append(" ").append(BotCommandUtils.toComplexBotCommandString("dl", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()), Integer.toString(i))).append("\n");
				}

				BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
//					Tuple<Integer,Integer> download = handler.download2(boardItem);

				return true;
			}

			// 선택한 게시물을 찾을 수 없는 경우, 사용자에게 에러 메시지츨 보낸다.
			BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "선택하신 게시물을 찾을 수 없습니다. 다시 조회 혹은 검색을 하여주세요. 문제가 계속해서 발생하사면 관리자에게 문의하세요.");
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
		if (this.board == null)
			throw new NullPointerException("board");
		if (this.torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (this.site == null)
			throw new NullPointerException("site");
	}

}
