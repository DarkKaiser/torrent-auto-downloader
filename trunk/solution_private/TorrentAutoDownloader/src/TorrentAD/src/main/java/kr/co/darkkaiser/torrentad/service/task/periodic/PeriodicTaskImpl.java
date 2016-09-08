package kr.co.darkkaiser.torrentad.service.task.periodic;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.task.AbstractTask;
import kr.co.darkkaiser.torrentad.service.task.TaskResult;
import kr.co.darkkaiser.torrentad.service.task.TaskType;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.board.WebSiteBoardItem;

public class PeriodicTaskImpl extends AbstractTask implements PeriodicTask {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(PeriodicTaskImpl.class);

	public PeriodicTaskImpl(WebSite site) {
		super(TaskType.PERIODIC, site);
	}

	@Override
	public TaskResult run(WebSiteHandler handler) throws Exception {
		if (handler == null) {
			throw new NullPointerException("handler");
		}

		validate();

		// @@@@@
		////////////////////////////////////////////////////////////////////////////////////
//		BogoBogoBoardItem bi = new BogoBogoBoardItem(BogoBogoBoard.ANI_ON, 0, "제목", "16-10-01");
//		bi.setDetailPageURL("board.php?board=newmovie&amp;bm=view&amp;no=28585&amp;category=&amp;auth=&amp;page=1&amp;search=&amp;keyword=&amp;recom=");
//		bi.addDownloadLink(DefaultBogoBogoBoardItemDownloadLink.newInstance("a", "b", "c", "d", "e", "f"));
//		System.out.println(bi);

		Iterator<WebSiteBoardItem> iterator = handler.search(this.searchContext);
		
//		/* 반환받은 정보를 이용해서 다운로드 */
//		/* 결과정보*/l.download(WebSiteBoardItem/*다운로드정보*/);
		// 다운로드 받은 파일을 업로드하는것은 다른 객체가 관리
			// 지정된 폴더내의 파일을 nas로 계속 업로드 하는 역할
		System.out.println("############# run");

		return TaskResult.OK;
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(PeriodicTaskImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
