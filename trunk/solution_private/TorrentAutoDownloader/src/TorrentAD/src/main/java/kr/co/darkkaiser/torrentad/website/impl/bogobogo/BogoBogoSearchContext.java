package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.website.AbstractWebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.WebSite;

public class BogoBogoSearchContext extends AbstractWebSiteSearchContext {
	
	private static final Logger logger = LoggerFactory.getLogger(BogoBogoSearchContext.class);

	private BogoBogoBoard board;

	public BogoBogoSearchContext() {
		super(WebSite.BOGOBOGO);
	}
	
	@Override
	public void setBoardName(String name) {
		try {
			this.board = BogoBogoBoard.fromString(name);
		} catch (Exception e) {
			// @@@@@
			logger.error("등록된 웹사이트의 이름('{}')이 유효하지 않습니다.", Constants.APP_CONFIG_TAG_WEBSITE_NAME);
			throw e;
		}
	}

	@Override
	public void validate() {
		super.validate();
		
		if (this.board == null) {
			throw new NullPointerException("board");
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoSearchContext.class.getSimpleName())
				.append("{")
				.append("board:").append(this.board)
				.append("}, ")
				.append(super.toString())
				.toString();
	}
	
}
