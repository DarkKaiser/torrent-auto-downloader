package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.filetransmission.FileTransmissionActionImpl;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.torrentsupervisorycontrol.TorrentSupervisoryControlActionImpl;

public final class ActionFactory {

	public static Action createAction(ActionType type, Configuration configuration) {
		if (type == ActionType.FILE_TRANSMISSION) {
			return new FileTransmissionActionImpl(configuration);
		} else if (type == ActionType.TORRENT_SUPERVISORY_CONTROL) {
			return new TorrentSupervisoryControlActionImpl(configuration);
		}

		throw new UnsupportedActionException(String.format("구현되지 않은 Action 타입(%s)입니다.", type));
	}

	private ActionFactory() {
	}

}
