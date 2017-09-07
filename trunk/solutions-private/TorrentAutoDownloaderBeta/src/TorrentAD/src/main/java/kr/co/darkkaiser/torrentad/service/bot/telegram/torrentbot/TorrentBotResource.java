package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.util.Disposable;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;

public interface TorrentBotResource extends Disposable {

	WebSite getSite();

	WebSiteConnector getSiteConnector();

	TorrentClient getTorrentClient();

}
