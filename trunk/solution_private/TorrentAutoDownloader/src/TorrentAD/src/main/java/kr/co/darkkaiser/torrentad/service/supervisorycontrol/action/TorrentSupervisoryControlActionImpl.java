package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	public TorrentSupervisoryControlActionImpl() {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL);
	}

	@Override
	public void init() {
		super.init();
		
		// @@@@@
	}

	@Override
	public void cleanup() {
		super.cleanup();
		
		// @@@@@
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		// @@@@@
	}

	@Override
	public void validate() {
		super.validate();
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
