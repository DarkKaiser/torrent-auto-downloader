package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.torrent.immediatelytaskaction;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.ad.task.immediately.AbstractImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class SearchBoardImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchBoardImmediatelyTaskAction.class);

	private final WebSiteConnector connector;
	
	public SearchBoardImmediatelyTaskAction(WebSiteConnector connector) {
		if (connector == null)
			throw new NullPointerException("connector");

		this.connector = connector;
	}

	@Override
	public String getName() {
		return String.format("%s > 전체 게시판 검색", this.connector.getSite().getName());
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// @@@@@
			WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();
			Iterator<WebSiteBoardItem> iterator = handler.searchAllBoards("드래곤");

			// @@@@@ 읽어드린 게시물 데이터를 클라이언트로 전송
			while (iterator.hasNext() == true) {
				WebSiteBoardItem next = iterator.next();
				System.out.println(next);
			}
		} catch (FailedLoadBoardItemsException e) {
			// @@@@@
//			logger.error("게시판 데이터를 로드하는 중에 예외가 발생하였습니다.", e);
			return false;
		} catch (Exception e) {
			logger.error(null, e);
			
			// @@@@@ 클라이언트로 조회실패 전송
			
			return false;
		}

		return true;
	}

	@Override
	public void validate() {
		super.validate();
		
		if (this.connector == null)
			throw new NullPointerException("connector");
	}

}
