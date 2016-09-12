package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

public final class ActionFactory {

	//	@@@@@
	// actionfactory.createFileUploadAction(), createTorrentAction -> 파일에 따라 해당하는 action을 생성해서 반환
	public static Action createAction() {
		return new DefaultFileUploadAction();
	}

	private ActionFactory() {
	}

}
