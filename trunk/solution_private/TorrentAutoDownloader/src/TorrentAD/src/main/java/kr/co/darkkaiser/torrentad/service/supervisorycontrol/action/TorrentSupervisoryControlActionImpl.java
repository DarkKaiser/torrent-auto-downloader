package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	public TorrentSupervisoryControlActionImpl() {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL);
	}

	@Override
	public void beforeExecute() {
		super.beforeExecute();
		
		// @@@@@
	}

	@Override
	public void afterExecute() {
		super.afterExecute();
		
		// @@@@@
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
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
