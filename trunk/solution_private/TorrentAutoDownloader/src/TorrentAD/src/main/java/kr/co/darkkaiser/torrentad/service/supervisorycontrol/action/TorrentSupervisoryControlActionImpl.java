package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	public TorrentSupervisoryControlActionImpl() {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL);
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
