package kr.co.darkkaiser.torrentad.website;

public interface WebSite<B extends WebSite<B>> extends WebSiteHandler, WebSiteContext<B> {

}
