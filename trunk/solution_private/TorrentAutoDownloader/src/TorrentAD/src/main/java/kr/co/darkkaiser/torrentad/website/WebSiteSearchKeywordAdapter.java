package kr.co.darkkaiser.torrentad.website;

import java.util.ArrayList;

public class WebSiteSearchKeywordAdapter implements WebSiteSearchKeyword {
	
	// @@@@@
	private final WebSiteSearchKeywordType type;

	// @@@@@
	private ArrayList<String> searchItem = new ArrayList<>();

	public WebSiteSearchKeywordAdapter(WebSiteSearchKeywordType type) {
		this.type = type;
	}

	// @@@@@
	public void add(String item) {
		this.searchItem.add(item);
	}

	// @@@@@
	public boolean isInclusion(String text) {
		return true;
	}

	// @@@@@
	public boolean isValid() {
		if (this.searchItem == null || this.searchItem.size() == 0) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		// @@@@@
		return super.toString();
	}

}
