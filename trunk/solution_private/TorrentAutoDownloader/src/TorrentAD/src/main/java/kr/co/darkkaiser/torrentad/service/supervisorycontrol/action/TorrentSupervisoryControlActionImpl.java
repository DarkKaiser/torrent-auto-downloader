package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	public TorrentSupervisoryControlActionImpl(AES256Util aes256, Configuration configuration) {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL, aes256, configuration);
	}

	@Override
	protected void beforeExecute() {
		// @@@@@
	}

	@Override
	protected void afterExecute() {
		// @@@@@
	}

	@Override
	protected void execute() throws Exception {
		// @@@@@
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
