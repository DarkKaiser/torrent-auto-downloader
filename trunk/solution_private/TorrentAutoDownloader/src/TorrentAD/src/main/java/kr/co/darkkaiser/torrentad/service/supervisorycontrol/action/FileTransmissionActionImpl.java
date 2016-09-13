package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter.FileTransmitter;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter.FtpFileTransmitter;
import kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter.TorrentFileTransmitter;

public class FileTransmissionActionImpl extends AbstractAction implements FileTransmissionAction {

	private static final Logger logger = LoggerFactory.getLogger(FileTransmissionActionImpl.class);

	private Map<File, Boolean/* 액션실행결과 */> files = new LinkedHashMap<>();

	private List<FileTransmitter> transmitters = new ArrayList<>();

	public FileTransmissionActionImpl() {
		super(ActionType.FILE_TRANSMISSION);
	}

	@Override
	public void init() {
		super.init();

		this.transmitters.add(new TorrentFileTransmitter());
		this.transmitters.add(new FtpFileTransmitter());
	}

	@Override
	public void cleanup() {
		super.cleanup();
		
		// @@@@@ 성공한 파일을 삭제한다.
		for (Map.Entry<File, Boolean> elem : this.files.entrySet()) {
//			System.out.println( String.format("키 : %s, 값 : %s", elem.getKey(), elem.getValue()) );
		}
		
		Iterator<FileTransmitter> iterator = this.transmitters.iterator();
		while (iterator.hasNext()) {
//			iterator.next().close();
		}

		this.transmitters.clear();
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		// @@@@@
	}

	@Override
	public boolean addFile(File file) {
		if (file == null)
			throw new NullPointerException("file");
		if (file.isFile() == false)
			throw new UnsupportedTransmissionFileException(file.getAbsolutePath());

		this.files.put(file, Boolean.FALSE);

		return true;
	}

	@Override
	public int getFileCount() {
		return this.files.size();
	}

	@Override
	public void validate() {
		super.validate();
		// @@@@@ validate 삭제???
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(FileTransmissionActionImpl.class.getSimpleName())
				.append("{")
				.append("filea:[");

		boolean firstKeyword = true;
		Iterator<File> iterator = this.files.keySet().iterator();
		while (iterator.hasNext()) {
			if (firstKeyword == false) {
				sb.append(",")
				  .append(iterator.next().getName());
			} else {
				firstKeyword = false;
				sb.append(iterator.next().getName());
			}
		}

		sb.append("]")
		  .append("}, ")
		  .append(super.toString());

		return sb.toString();
	}

}
