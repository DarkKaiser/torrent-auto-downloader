package com.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import com.darkkaiser.torrentad.net.torrent.TorrentClient;
import com.darkkaiser.torrentad.util.Disposable;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteConnector;

public interface TorrentBotResource extends Disposable {

	WebSite getSite();

	WebSiteConnector getSiteConnector();

	TorrentClient getTorrentClient();

}
