package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItemIdentifierDescCompare;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class SearchBoardImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchBoardImmediatelyTaskAction.class);

	private final WebSiteConnector connector;
	
	private final WebSiteBoard board;

	public SearchBoardImmediatelyTaskAction(WebSiteConnector connector, WebSiteBoard board) {
		if (connector == null)
			throw new NullPointerException("connector");
		if (board == null)
			throw new NullPointerException("board");

		this.board = board;
		this.connector = connector;
	}

	@Override
	public String getName() {
		return String.format("%s > %s 검색", this.connector.getSite().getName(), this.board.getDescription());
	}

	@Override
	public Boolean call() throws Exception {
		try {
			// @@@@@ connector에서 로그인이 안 되어있을때의 처리, 오랜 경과시간 이후의 처리
			
			WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();
			Iterator<WebSiteBoardItem> iterator = handler.searchNow(this.board, "드래곤", new WebSiteBoardItemIdentifierDescCompare());

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
		if (this.board == null)
			throw new NullPointerException("board");
	}

}
