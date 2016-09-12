package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

// @@@@@
public class TorrentSupervisoryControlAction extends AbstractAction {

	public TorrentSupervisoryControlAction() {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(TorrentSupervisoryControlAction.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
