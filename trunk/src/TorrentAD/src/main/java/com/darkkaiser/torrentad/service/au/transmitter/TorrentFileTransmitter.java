package com.darkkaiser.torrentad.service.au.transmitter;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.net.torrent.TorrentClient;
import com.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

@Slf4j
public class TorrentFileTransmitter extends AbstractFileTransmitter {

	private TorrentClient torrentClient;

	public TorrentFileTransmitter(final Configuration configuration) {
		super(configuration);
	}

	@Override
	public void prepare() throws Exception {
		if (this.torrentClient != null && this.torrentClient.isConnected() == true) 
			return;

		String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
		String password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD));

		this.torrentClient = new TransmissionRpcClient(url);
		if (this.torrentClient.connect(id, password) == false)
			log.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
	}

	@Override
	public boolean transmit(final File file) throws Exception {
		Objects.requireNonNull(file, "file");
		Objects.requireNonNull(this.torrentClient, "torrentClient");

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
		//noinspection Duplicates
		if (this.torrentClient != null) {
			try {
				this.torrentClient.disconnect();
			} catch (final Exception e) {
				log.error(null, e);
			}

			this.torrentClient = null;
		}

		return true;
	}

	@Override
	public boolean support(File file) {
		Objects.requireNonNull(file, "file");

		return file.isDirectory() != true && file.getName().toLowerCase().endsWith(".torrent");
	}

}
