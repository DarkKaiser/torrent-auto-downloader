package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.jsoup.helper.StringUtil;

public abstract class AbstractWebSiteSearchHistoryData implements WebSiteSearchHistoryData {

	// @@@@@ 처음 생성될때 값 로드
	private final static AtomicLong atomicIdentifier = new AtomicLong(0);

	protected final long identifier;

	protected final WebSiteBoard board;

	protected final String keyword;
	
	protected final List<WebSiteBoardItem> results;

	public AbstractWebSiteSearchHistoryData(WebSiteBoard board, String keyword, List<WebSiteBoardItem> results) {
		if (board == null)
			throw new NullPointerException("board");
		if (results == null)
			throw new NullPointerException("results");
		if (StringUtil.isBlank(keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");

		this.identifier = AbstractWebSiteSearchHistoryData.atomicIdentifier.incrementAndGet();
		// @@@@@ identifier 값 저장
		
		this.board = board;
		this.keyword = keyword;
		this.results = results;
	}

	@Override
	public long getIdentifier() {
		return this.identifier;
	}

	@Override
	public WebSiteBoard getBoard() {
		return this.board;
	}

	@Override
	public String getKeyword() {
		return this.keyword;
	}
	
	@Override
	public Iterator<WebSiteBoardItem> resultIterator() {
		return this.results.iterator();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractWebSiteSearchHistoryData.class.getSimpleName())
				.append("{")
				.append("identifier:").append(getIdentifier())
				.append(", board:").append(getBoard())
				.append(", keyword:").append(getKeyword())
				.append("}")
				.toString();
	}

}
