package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.Iterator;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandConstants;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.BotCommandUtils;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.AbstractRequestHandler;
import kr.co.darkkaiser.torrentad.util.Tuple;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemComparatorIdentifierDesc;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemDownloadLink;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoardItem;

// @@@@@
public class WebSiteBoardItemDownloadRequestHandler extends AbstractRequestHandler {

	private final TorrentBotResource torrentBotResource;

	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;
	
	public WebSiteBoardItemDownloadRequestHandler(TorrentBotResource torrentBotResource, ImmediatelyTaskExecutorService immediatelyTaskExecutorService) {
		super(BotCommandConstants.INLINE_COMMAND_DOWNLOAD);

		if (torrentBotResource == null)
			throw new NullPointerException("torrentBotResource");
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");

		this.torrentBotResource = torrentBotResource;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;
	}
	
	@Override
	public boolean executable(String command, String[] parameters, boolean containInitialChar) {
		if (super.executable0(command, parameters, 3, 3) == false)
			return false;

		return true;
	}

	// @@@@@
	@Override
	public void execute(AbsSender absSender, ChatRoom chatRoom, Update update, String command, String[] parameters, boolean containInitialChar) {
		WebSiteBoard board = BogoBogoBoard.MOVIE_NEW;
		long key = Long.parseLong(parameters[1]);
		long index = Long.parseLong(parameters[2]);

		WebSiteHandler handler = (WebSiteHandler) this.torrentBotResource.getSiteConnector().getConnection();
		
		try {
			Iterator<WebSiteBoardItem> iterator = handler.list(board, false, new WebSiteBoardItemComparatorIdentifierDesc());
			while (iterator.hasNext() == true) {
				WebSiteBoardItem boardItem = iterator.next();
				
				if (boardItem.getIdentifier() == key) {
					// 다운로드
					BogoBogoBoardItem bogobogoBoardItem = (BogoBogoBoardItem) boardItem;
					Iterator<WebSiteBoardItemDownloadLink> downloadLinkIterator = bogobogoBoardItem.downloadLinkIterator();
					if (downloadLinkIterator.hasNext() == false) {
						BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "다운로드 할 첨부파일을 읽을 수 업습니다.");
						return;
					}
					
					Tuple<Integer,Integer> download = handler.download2(boardItem, index);

					BotCommandUtils.sendMessage(absSender, chatRoom.getChatId(), "파일을 다운로드합니다.");
					
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
				.append(WebSiteBoardItemDownloadRequestHandler.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
