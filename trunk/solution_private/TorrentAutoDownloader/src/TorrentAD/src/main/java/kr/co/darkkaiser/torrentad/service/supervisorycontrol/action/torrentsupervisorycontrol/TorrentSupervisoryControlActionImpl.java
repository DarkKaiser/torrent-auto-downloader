package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.torrentsupervisorycontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.AbstractAction;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.ActionType;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	private static final Logger logger = LoggerFactory.getLogger(TorrentSupervisoryControlActionImpl.class);

	private TorrentClient torrentClient;
	
	private AES256Util aes256;
	
	public TorrentSupervisoryControlActionImpl(Configuration configuration) {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL, configuration);
	}

	@Override
	protected void beforeExecute() {
		if (this.torrentClient != null && this.torrentClient.isConnected() == true) 
			return;

		// @@@@@
		String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
		String password = null;
		try {
			// @@@@@
			password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.torrentClient = new TransmissionRpcClient(url);
		try {
			if (this.torrentClient.connect(id, password) == false)
				logger.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// @@@@@
	}

	@Override
	protected void afterExecute() {
		if (this.torrentClient != null) {
			try {
				this.torrentClient.disconnect();
			} catch (Exception e) {
				logger.error(null, e);
			}

			this.torrentClient = null;
		}
	}

	@Override
	protected void execute() throws Exception {
		if (this.torrentClient == null)
			throw new NullPointerException("torrentClient");
		if (this.torrentClient.isConnected() == false)
			throw new IllegalStateException("토렌트 서버에 연결되어 있지 않습니다.");

		// @@@@@
		this.torrentClient.get();

	}

	protected String decode(String encryption) throws Exception {
		if (this.aes256 == null)
			this.aes256 = new AES256Util();

		try {
			return this.aes256.decode(encryption);
		} catch (Exception e) {
			logger.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", encryption);
			throw e;
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(TorrentSupervisoryControlActionImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
