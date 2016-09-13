package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@@@@@ 토렌트 파일은 정지상태로 올리기
//transmission rpc
//https://github.com/stil4m/transmission-rpc-java
//https://sourceforge.net/projects/transmission-rj/
public class FileTransmissionActionImpl extends AbstractAction implements FileTransmissionAction {

	private static final Logger logger = LoggerFactory.getLogger(FileTransmissionActionImpl.class);
	
	protected Map<File, Boolean/* 액션실행결과 */> files = new LinkedHashMap<>();

	public FileTransmissionActionImpl() {
		super(ActionType.FILE_TRANSMISSION);
	}

	@Override
	public void init() {
		// @@@@@
	}

	@Override
	public void cleanup() {
		// @@@@@
	}

	@Override
	public void execute() throws Exception {
		// @@@@@
	}

	@Override
	public boolean addFile(File file) {
		if (file == null)
			throw new NullPointerException("file");
		if (file.isFile() == false)
			throw new UnsupportedTransmissionFileException(file.getAbsolutePath());

		return this.files.put(file, false);
	}

	@Override
	public int getFileCount() {
		return this.files.size();
	}

	@Override
	public void validate() {
		super.validate();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(FileTransmissionActionImpl.class.getSimpleName())
				.append("{")
				.append("fileCount:").append(getFileCount())
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
