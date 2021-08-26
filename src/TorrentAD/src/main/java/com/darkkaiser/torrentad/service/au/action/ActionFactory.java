package com.darkkaiser.torrentad.service.au.action;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.au.action.supervisorycontrol.TorrentSupervisoryControlActionImpl;
import com.darkkaiser.torrentad.service.au.action.transmission.FileTransmissionActionImpl;

public final class ActionFactory {

	public static Action createAction(final ActionType type, final Configuration configuration) {
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
