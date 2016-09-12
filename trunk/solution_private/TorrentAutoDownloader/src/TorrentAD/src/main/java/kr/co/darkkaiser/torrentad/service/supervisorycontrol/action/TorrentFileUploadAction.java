package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

// @@@@@ 토렌트 파일은 정지상태로 올리기
//transmission rpc
//https://github.com/stil4m/transmission-rpc-java
//https://sourceforge.net/projects/transmission-rj/
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
