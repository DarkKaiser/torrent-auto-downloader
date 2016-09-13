package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public final class ActionFactory {

	public static Action createAction(ActionType type) {
		if (type == ActionType.FILE_TRANSMISSION) {
			return new FileTransmissionActionImpl();
		} else if (type == ActionType.TORRENT_SUPERVISORY_CONTROL) {
			return new TorrentSupervisoryControlActionImpl();
		}

		throw new UnsupportedActionException(String.format("구현되지 않은 Action 타입(%s)입니다.", type));
	}

	private ActionFactory() {
	}

}
