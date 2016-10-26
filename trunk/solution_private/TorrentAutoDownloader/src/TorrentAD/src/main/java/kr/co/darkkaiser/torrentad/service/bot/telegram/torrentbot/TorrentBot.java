package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;

// @@@@@
public interface TorrentBot {
	
	WebSiteConnector getConnector();
	
	TorrentClient getTorrentClient();
	
}
