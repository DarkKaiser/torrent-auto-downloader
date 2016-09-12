package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

// @@@@@
public class DefaultFileUploadAction extends FileUploadAction {

	public DefaultFileUploadAction() {
		super(ActionType.DEFAULT_FILE_UPLOAD);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(DefaultFileUploadAction.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
