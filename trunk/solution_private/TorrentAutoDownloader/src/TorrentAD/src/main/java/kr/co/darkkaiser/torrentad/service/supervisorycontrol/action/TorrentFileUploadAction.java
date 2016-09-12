package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

// @@@@@
public class TorrentFileUploadAction extends FileUploadAction {

	public TorrentFileUploadAction() {
		super(ActionType.TORRENT_FILE_UPLOAD);
	}
	
	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(TorrentFileUploadAction.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
