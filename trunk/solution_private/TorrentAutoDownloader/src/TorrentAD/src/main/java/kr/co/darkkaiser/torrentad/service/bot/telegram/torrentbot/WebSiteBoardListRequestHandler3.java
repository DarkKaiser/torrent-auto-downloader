package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.Iterator;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.AbstractRequestHandler;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItem;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItemDownloadLink;

// @@@@@
public class WebSiteBoardListRequestHandler3 extends AbstractRequestHandler {

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardListRequestHandler3(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super("ls");

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 2, 2) == false)
			return false;

		return true;
	}

	// @@@@@
	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, String command, String[] parameters, boolean containInitialChar, Update update) {
		WebSiteBoard board = BogoBogoBoard.MOVIE_NEW;
		long key = Long.parseLong(parameters[1]);

		WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
		
		try {
			Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemIdentifierDescCompare());
			while (iterator.hasNext() == true) {
				WebSiteBoardItem boardItem = iterator.next();
				
				if (boardItem.getIdentifier() == key) {
					// 다운로드
					BogoBogoBoardItem bogobogoBoardItem = (BogoBogoBoardItem) boardItem;
					Iterator<BogoBogoBoardItemDownloadLink> downloadLinkIterator = bogobogoBoardItem.downloadLinkIterator();
					if (downloadLinkIterator.hasNext() == false) {
						handler.download3(bogobogoBoardItem);
					}
					
					downloadLinkIterator = bogobogoBoardItem.downloadLinkIterator();
					if (downloadLinkIterator.hasNext() == false) {
						sendAnswerMessage(absSender, chatRoom.getChatId(), "다운로드 할 첨부파일을 읽을 수 업습니다.");
						return;
					}
					
					StringBuilder sbAnswerMessage = new StringBuilder();
					sbAnswerMessage.append("어떤 파일에 대한 첨부파일 조회가 완료되었습니다.\n\n");

					int i = 0;
					while (downloadLinkIterator.hasNext() == true) {
						BogoBogoBoardItemDownloadLink next = downloadLinkIterator.next();

						++i;
						sbAnswerMessage.append(next.getFileName()).append(", 확장자:").append(next.getValue4()).append(" ").append(BotCommandUtils.toComplexBotCommandString("dl", boardItem.getBoard().getCode(), Long.toString(boardItem.getIdentifier()), Integer.toString(i))).append("\n");
					}

					sendAnswerMessage(absSender, chatRoom.getChatId(), sbAnswerMessage.toString());
//					Tuple<Integer,Integer> download = handler.download2(boardItem);
					
					return;
				}
			}
			
		} catch (FailedLoadBoardItemsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(WebSiteBoardListRequestHandler3.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
