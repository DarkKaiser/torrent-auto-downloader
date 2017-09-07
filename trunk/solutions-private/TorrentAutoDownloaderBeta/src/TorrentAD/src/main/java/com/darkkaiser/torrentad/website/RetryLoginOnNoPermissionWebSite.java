package com.darkkaiser.torrentad.website;

import java.util.Comparator;
import java.util.Iterator;

import com.darkkaiser.torrentad.util.Tuple;

public final class RetryLoginOnNoPermissionWebSite implements WebSiteConnection, WebSiteHandler, WebSiteContext {

	private final AbstractWebSite site;

	public RetryLoginOnNoPermissionWebSite(AbstractWebSite site) {
		this.site = site;
	}

	@Override
	public void login(WebSiteAccount account) throws Exception {
		this.site.login(account);
	}

	@Override
	public void logout() throws Exception {
		this.site.logout();
	}

	@Override
	public boolean isLogin() {
		return this.site.isLogin();
	}

	@Override
	public String getName() {
		return this.site.getName();
	}

	@Override
	public WebSiteAccount getAccount() {
		return this.site.getAccount();
	}

	@Override
	public void setAccount(WebSiteAccount account) {
		this.site.setAccount(account);
	}

	@Override
	public Iterator<WebSiteBoardItem> list(WebSiteBoard board, boolean loadNow, Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
		assert isLogin() == true;
		
		try {
			return this.site.list(board, loadNow, comparator);
		} catch (NoPermissionException e) {
			// 일정 시간이 지나 서버에서 로그아웃되어, 게시판에 대한 접근 권한이 없는 경우 다시 로그인하도록 한다.
			WebSiteConnector siteConnector = this.site.getSiteConnector();
			if (siteConnector == null)
				throw e;
			
			siteConnector.logout();
			siteConnector.login();

			return this.site.list(board, loadNow, comparator);
		}
	}

	@Override
	public Iterator<WebSiteBoardItem> listAndFilter(WebSiteSearchContext searchContext, boolean loadNow, Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
		assert isLogin() == true;
		
		try {
			return this.site.listAndFilter(searchContext, loadNow, comparator);
		} catch (NoPermissionException e) {
			// 일정 시간이 지나 서버에서 로그아웃되어, 게시판에 대한 접근 권한이 없는 경우 다시 로그인하도록 한다.
			WebSiteConnector siteConnector = this.site.getSiteConnector();
			if (siteConnector == null)
				throw e;
			
			siteConnector.logout();
			siteConnector.login();

			return this.site.listAndFilter(searchContext, loadNow, comparator);
		}
	}

	@Override
	public Tuple<String/* 검색기록 Identifier */, Iterator<WebSiteBoardItem>/* 검색결과목록 */> search(WebSiteBoard board, String keyword, Comparator<? super WebSiteBoardItem> comparator) throws NoPermissionException, LoadBoardItemsException {
		assert isLogin() == true;
		
		try {
			return this.site.search(board, keyword, comparator);
		} catch (NoPermissionException e) {
			// 일정 시간이 지나 서버에서 로그아웃되어, 게시판에 대한 접근 권한이 없는 경우 다시 로그인하도록 한다.
			WebSiteConnector siteConnector = this.site.getSiteConnector();
			if (siteConnector == null)
				throw e;
			
			siteConnector.logout();
			siteConnector.login();

			return this.site.search(board, keyword, comparator);
		}
	}

	@Override
	public WebSiteSearchResultData getSearchResultData(String identifier) {
		return this.site.getSearchResultData(identifier);
	}

	@Override
	public Tuple<Integer, Integer> download(WebSiteBoardItem boardItem, WebSiteSearchContext searchContext) throws NoPermissionException {
		assert isLogin() == true;
		
		try {
			return this.site.download(boardItem, searchContext);
		} catch (NoPermissionException e) {
			// 일정 시간이 지나 서버에서 로그아웃되어, 게시판에 대한 접근 권한이 없는 경우 다시 로그인하도록 한다.
			WebSiteConnector siteConnector = this.site.getSiteConnector();
			if (siteConnector == null)
				throw e;
			
			siteConnector.logout();
			siteConnector.login();

			return this.site.download(boardItem, searchContext);
		}
	}

	@Override
	public Tuple<Integer, Integer> download(WebSiteBoardItem boardItem, long downloadLinkIndex) throws NoPermissionException {
		assert isLogin() == true;
		
		try {
			return this.site.download(boardItem, downloadLinkIndex);
		} catch (NoPermissionException e) {
			// 일정 시간이 지나 서버에서 로그아웃되어, 게시판에 대한 접근 권한이 없는 경우 다시 로그인하도록 한다.
			WebSiteConnector siteConnector = this.site.getSiteConnector();
			if (siteConnector == null)
				throw e;
			
			siteConnector.logout();
			siteConnector.login();

			return this.site.download(boardItem, downloadLinkIndex);
		}
	}

	@Override
	public boolean loadDownloadLink(WebSiteBoardItem boardItem) throws NoPermissionException {
		assert isLogin() == true;
		
		try {
			return this.site.loadDownloadLink(boardItem);
		} catch (NoPermissionException e) {
			// 일정 시간이 지나 서버에서 로그아웃되어, 게시판에 대한 접근 권한이 없는 경우 다시 로그인하도록 한다.
			WebSiteConnector siteConnector = this.site.getSiteConnector();
			if (siteConnector == null)
				throw e;
			
			siteConnector.logout();
			siteConnector.login();

			return this.site.loadDownloadLink(boardItem);
		}
	}
	
	@Override
	public String toString() {
		return this.site.toString();
	}

}
