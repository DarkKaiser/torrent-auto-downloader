package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

// @@@@@
public abstract class FileUploadAction extends AbstractAction {

	protected Map<File, Boolean/* 액션 실행결과 */> files = new HashMap<>();

	protected FileUploadAction(ActionType actionType) {
		super(actionType);
	}
	
	public boolean addFile(File file) {
		if (file == null) {
			throw new NullPointerException("file");
		}

		return this.files.put(file, false);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(FileUploadAction.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
