package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

//@@@@@ 토렌트 파일은 정지상태로 올리기
//transmission rpc
//https://github.com/stil4m/transmission-rpc-java
//https://sourceforge.net/projects/transmission-rj/
public class FileTransmissionAction extends AbstractAction {

	protected Map<File, Boolean/* 액션실행결과 */> files = new HashMap<>();

	public FileTransmissionAction() {
		super(ActionType.FILE_TRANSMISSION);
	}
	
	// @@@@@
	public boolean addFile(File file) {
		if (file == null)
			throw new NullPointerException("file");

		return this.files.put(file, false);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(FileTransmissionAction.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
