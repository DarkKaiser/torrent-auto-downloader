package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.AbstractImmediatelyTaskAction;

public class GetTorrentImmediatelyTaskAction extends AbstractImmediatelyTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(GetTorrentImmediatelyTaskAction.class);
	
	private final TorrentClient torrentClient;

	public GetTorrentImmediatelyTaskAction(TorrentClient torrentClient) {
		if (torrentClient == null)
			throw new NullPointerException("torrentClient");

		this.torrentClient = torrentClient;
	}

	@Override
	public String getName() {
		return "토렌트서버 상태조회";
	}

	@Override
	public Boolean call() throws Exception {
		// @@@@@
		try {
			TorrentGetMethodResult torrent = this.torrentClient.getTorrent();
			System.out.println(torrent);
			
			// 사용자에게 전달

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}

		return true;
	}

	@Override
	public void validate() {
		super.validate();
		
		if (this.torrentClient == null)
			throw new NullPointerException("torrentClient");
	}

}
