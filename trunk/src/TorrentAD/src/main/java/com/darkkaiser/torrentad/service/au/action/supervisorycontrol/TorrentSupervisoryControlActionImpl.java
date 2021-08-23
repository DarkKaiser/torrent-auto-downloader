package com.darkkaiser.torrentad.service.au.action.supervisorycontrol;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.net.torrent.TorrentClient;
import com.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import com.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import com.darkkaiser.torrentad.service.au.action.AbstractAction;
import com.darkkaiser.torrentad.service.au.action.ActionType;
import com.darkkaiser.torrentad.util.crypto.AES256Util;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	private TorrentClient torrentClient;
	
	private final int maxConcurrentDownloadingTorrentCount;

	public TorrentSupervisoryControlActionImpl(final Configuration configuration) {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL, configuration);

		this.maxConcurrentDownloadingTorrentCount = Integer.parseInt(this.configuration.getValue(Constants.APP_CONFIG_TAG_MAX_CONCURRENT_DOWNLOADING_TORRENT_COUNT));
	}

	@Override
	protected boolean beforeExecute() {
		if (this.torrentClient != null && this.torrentClient.isConnected() == true) 
			return true;

		String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
		String password = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD);

		try {
			password = new AES256Util().decode(password);
		} catch (final Exception e) {
			log.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", password);
			return false;
		}

		this.torrentClient = new TransmissionRpcClient(url);

		try {
			if (this.torrentClient.connect(id, password) == false)
				log.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
		} catch (final Exception e) {
			log.error("토렌트 서버 접속이 실패하였습니다.", e);
			return false;
		}
		
		return true;
	}

	@Override
	protected void afterExecute() {
		//noinspection Duplicates
		if (this.torrentClient != null) {
			try {
				this.torrentClient.disconnect();
			} catch (final Exception e) {
				log.error(null, e);
			}

			this.torrentClient = null;
		}
	}

	@Override
	protected void execute() throws Exception {
		Objects.requireNonNull(this.torrentClient, "torrentClient");

		if (this.torrentClient.isConnected() == false)
			throw new IllegalStateException("토렌트 서버에 연결되어 있지 않습니다.");

		try {
			TorrentGetMethodResult methodResult = this.torrentClient.getTorrent();
			if (methodResult != null) {
				int downloadingCount = 0;
				List<Long> ids = new ArrayList<>();
				for (final TorrentGetMethodResult.Torrent torrent : methodResult.arguments.torrents) {
					switch (torrent.status()) {
						case 0:        // Stopped
							if (torrent.isFinished() == false && torrent.error() == 0)
								ids.add(torrent.getId());
							break;

						case 1:        // Check waiting
						case 2:        // Checking
						case 3:        // Download waiting
						case 4:        // Downloading
							++downloadingCount;
							break;

						case 5:        // Seed waiting
						case 6:        // Seeding
							break;

						default:
							break;
					}
				}
				
				if (ids.isEmpty() == false && downloadingCount < this.maxConcurrentDownloadingTorrentCount) {
					int possibleDownloadCount = this.maxConcurrentDownloadingTorrentCount - downloadingCount;

					while (ids.size() > possibleDownloadCount) {
						ids.remove(ids.size() - 1);
					}

					this.torrentClient.startTorrent(ids);
				}
			}
		} catch (final Exception e) {
			log.error("토렌트 서버의 감시 및 제어 작업이 실패하였습니다.", e);
		}
	}

	@Override
	public String toString() {
		return TorrentSupervisoryControlActionImpl.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
