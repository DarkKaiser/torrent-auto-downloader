package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.AbstractWebSiteSearchContext;

// @@@@@
public class BogoBogoWebSiteSearchContext extends AbstractWebSiteSearchContext {
	
	private BogoBogoBoard board;

	@Override
	public void setBoardName(String boardName) {
		if (boardName == null) {
			throw new NullPointerException("boardName");
		}
		if (StringUtil.isBlank(boardName) == true) {
			throw new IllegalArgumentException("boardName은 빈 문자열을 허용하지 않습니다.");
		}

		// 지원되는 보드인지 확인, forString
		// @@@@@
		board = BogoBogoBoard.NEW_MOVIE;
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		super.validate();
	}

	@Override
	public boolean isValid() {
		if (super.isValid() == false) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// @@@@@
		return super.toString();
	}
	
	// @@@@@
	
}
