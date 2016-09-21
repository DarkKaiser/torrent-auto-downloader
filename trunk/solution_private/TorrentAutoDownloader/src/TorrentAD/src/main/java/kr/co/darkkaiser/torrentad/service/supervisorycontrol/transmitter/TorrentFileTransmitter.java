package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;

public class TorrentFileTransmitter extends AbstractFileTransmitter {

	private static final Logger logger = LoggerFactory.getLogger(TorrentFileTransmitter.class);

	private TorrentClient torrentClient;

	public TorrentFileTransmitter(Configuration configuration) {
		super(configuration);
	}

	@Override
	public void prepare() throws Exception {
		if (this.torrentClient != null && this.torrentClient.isConnected() == true) 
			return;

		String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
		String password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD));

		this.torrentClient = new TransmissionRpcClient();
		if (this.torrentClient.connect(url, id, password) == false)
			logger.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
	}

	@Override
	public boolean transmit(File file) throws Exception {
		if (file == null)
			throw new NullPointerException("file");
		if (this.torrentClient == null)
			throw new NullPointerException("torrentClient");
		if (this.torrentClient.isConnected() == false)
			throw new IllegalStateException("토렌트 서버에 연결되어 있지 않습니다.");

		assert file.isDirectory() == false;

		if (file.exists() == false) {
			throw new FileNotFoundException(file.getAbsolutePath());
		} else {
			return this.torrentClient.addTorrent(file, true);
		}
	}

	@Override
	public boolean transmitFinished() {
		if (this.torrentClient != null) {
			try {
				this.torrentClient.disconnect();
			} catch (Exception e) {
				logger.error(null, e);
			}

			this.torrentClient = null;
		}

		return true;
	}

	@Override
	public boolean support(File file) {
		if (file == null)
			throw new NullPointerException("file");
		
		if (file.isDirectory() == true)
			return false;

		return file.getName().toLowerCase().endsWith(".torrent");
	}

}
