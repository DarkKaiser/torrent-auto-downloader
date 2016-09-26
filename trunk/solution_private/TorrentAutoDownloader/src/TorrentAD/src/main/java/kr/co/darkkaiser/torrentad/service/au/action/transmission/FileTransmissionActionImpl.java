package kr.co.darkkaiser.torrentad.service.au.action.transmission;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.au.action.AbstractAction;
import kr.co.darkkaiser.torrentad.service.au.action.ActionType;
import kr.co.darkkaiser.torrentad.service.au.action.UnsupportedTransmissionFileException;
import kr.co.darkkaiser.torrentad.service.au.transmitter.FTPFileTransmitter;
import kr.co.darkkaiser.torrentad.service.au.transmitter.FileTransmitter;
import kr.co.darkkaiser.torrentad.service.au.transmitter.TorrentFileTransmitter;

public class FileTransmissionActionImpl extends AbstractAction implements FileTransmissionAction {

	private static final Logger logger = LoggerFactory.getLogger(FileTransmissionActionImpl.class);

	private Map<File, Boolean/* 액션실행결과 */> files = new LinkedHashMap<>();

	private List<FileTransmitter> fileTransmitters = new ArrayList<>();

	public FileTransmissionActionImpl(Configuration configuration) {
		super(ActionType.FILE_TRANSMISSION, configuration);
	}

	@Override
	protected boolean beforeExecute() {
		this.fileTransmitters.add(new TorrentFileTransmitter(this.configuration));
		this.fileTransmitters.add(new FTPFileTransmitter(this.configuration));
		return true;
	}

	@Override
	protected void afterExecute() {
		Iterator<FileTransmitter> iterator = this.fileTransmitters.iterator();
		while (iterator.hasNext()) {
			iterator.next().transmitFinished();
		}

		this.fileTransmitters.clear();

		// 전송이 성공한 파일들은 삭제한다.
		for (Map.Entry<File, Boolean> entry : this.files.entrySet()) {
			if (entry.getValue() == true) {
				logger.debug("{} 파일의 전송이 완료되어 삭제합니다.", entry.getKey().getName());
				entry.getKey().delete();
			}
		}
	}

	@Override
	protected void execute() throws Exception {
		for (Map.Entry<File, Boolean> entry : this.files.entrySet()) {
			File file = entry.getKey();

			assert entry.getValue() == false;

			try {
				Iterator<FileTransmitter> iterator = this.fileTransmitters.iterator();
				while (iterator.hasNext()) {
					FileTransmitter transmitter = iterator.next();
					if (transmitter.support(file) == true) {
						transmitter.prepare();
						if (transmitter.transmit(file) == true) {
							entry.setValue(true);
							logger.debug("{} 파일의 전송이 완료되었습니다.", file.getName());
						} else {
							logger.warn("{} 파일의 전송이 실패하였습니다.", file.getName());
						}

						break;
					}
				}
			} catch (Exception e) {
				logger.error("파일을 전송하는 도중에 예외가 발생하였습니다.({})", file.getName(), e);
			}
		}
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
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(FileTransmissionActionImpl.class.getSimpleName())
				.append("{")
				.append("files:[");

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
