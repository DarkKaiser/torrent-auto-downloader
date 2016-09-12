package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// @@@@@
public abstract class FileUploadAction extends AbstractAction {

	protected List<File> files = new ArrayList<>();

	protected FileUploadAction(ActionType actionType) {
		super(actionType);
	}
	
	public void addFile(File file) {
		// @@@@@
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
