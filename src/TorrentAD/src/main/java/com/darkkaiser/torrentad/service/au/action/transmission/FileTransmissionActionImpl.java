package com.darkkaiser.torrentad.service.au.action.transmission;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.au.action.AbstractAction;
import com.darkkaiser.torrentad.service.au.action.ActionType;
import com.darkkaiser.torrentad.service.au.action.UnsupportedTransmissionFileException;
import com.darkkaiser.torrentad.service.au.transmitter.FTPFileTransmitter;
import com.darkkaiser.torrentad.service.au.transmitter.FileTransmitter;
import com.darkkaiser.torrentad.service.au.transmitter.TorrentFileTransmitter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j
public class FileTransmissionActionImpl extends AbstractAction implements FileTransmissionAction {

	private final Map<File, Boolean/* 액션실행결과 */> files = new LinkedHashMap<>();

	private final List<FileTransmitter> fileTransmitters = new ArrayList<>();

	public FileTransmissionActionImpl(final Configuration configuration) {
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
		for (final FileTransmitter fileTransmitter : this.fileTransmitters) {
			fileTransmitter.transmitFinished();
		}

		this.fileTransmitters.clear();

		// 전송이 성공한 파일들은 삭제한다.
		for (final Map.Entry<File, Boolean> entry : this.files.entrySet()) {
			if (entry.getValue() == true) {
				log.debug("{} 파일의 전송이 완료되어 삭제합니다.", entry.getKey().getName());

                //noinspection ResultOfMethodCallIgnored
                entry.getKey().delete();
			}
		}
	}

	@Override
	protected void execute() throws Exception {
		for (final Map.Entry<File, Boolean> entry : this.files.entrySet()) {
			File file = entry.getKey();

			assert entry.getValue() == false;

			try {
				for (final FileTransmitter transmitter : this.fileTransmitters) {
					if (transmitter.support(file) == true) {
						transmitter.prepare();
						if (transmitter.transmit(file) == true) {
							entry.setValue(true);
							log.debug("{} 파일의 전송이 완료되었습니다.", file.getName());
						} else {
							log.warn("{} 파일의 전송이 실패하였습니다.", file.getName());
						}

						break;
					}
				}
			} catch (final Exception e) {
				log.error("파일을 전송하는 도중에 예외가 발생하였습니다.({})", file.getName(), e);
			}
		}
	}

	@Override
	public boolean addFile(final File file) {
		Objects.requireNonNull(file, "file");

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
