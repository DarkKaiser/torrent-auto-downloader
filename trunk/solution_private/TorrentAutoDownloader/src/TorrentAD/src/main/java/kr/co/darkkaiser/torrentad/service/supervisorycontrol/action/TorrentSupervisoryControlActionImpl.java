package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import kr.co.darkkaiser.torrentad.config.Configuration;

public class TorrentSupervisoryControlActionImpl extends AbstractAction implements TorrentSupervisoryControlAction {

	public TorrentSupervisoryControlActionImpl(Configuration configuration) {
		super(ActionType.TORRENT_SUPERVISORY_CONTROL, configuration);
	}

	@Override
	protected void beforeExecute() {
		// @@@@@
//		this.transmitters.add(new TorrentFileTransmitter(this.configuration));
//		this.transmitters.add(new FTPFileTransmitter(this.configuration));
	}

	@Override
	protected void afterExecute() {
		// @@@@@
//		Iterator<FileTransmitter> iterator = this.transmitters.iterator();
//		while (iterator.hasNext()) {
//			iterator.next().transmitFinished();
//		}
//
//		this.transmitters.clear();
//
//		// 전송이 성공한 파일들은 삭제한다.
//		for (Map.Entry<File, Boolean> entry : this.files.entrySet()) {
//			if (entry.getValue() == true) {
//				logger.debug("{} 파일의 전송이 완료되어 삭제합니다.", entry.getKey().getName());
//				entry.getKey().delete();
//			}
//		}
	}

	@Override
	protected void execute() throws Exception {
		// @@@@@
//		for (Map.Entry<File, Boolean> entry : this.files.entrySet()) {
//			File file = entry.getKey();
//
//			assert entry.getValue() == false;
//
//			try {
//				Iterator<FileTransmitter> iterator = this.transmitters.iterator();
//				while (iterator.hasNext()) {
//					FileTransmitter transmitter = iterator.next();
//					if (transmitter.support(file) == true) {
//						transmitter.prepare();
//						if (transmitter.transmit(file) == true) {
//							entry.setValue(true);
//							logger.debug("{} 파일의 전송이 완료되었습니다.", file.getName());
//						} else {
//							logger.warn("{} 파일의 전송이 실패하였습니다.", file.getName());
//						}
//
//						break;
//					}
//				}
//			} catch (Exception e) {
//				logger.error("파일을 전송하는 도중에 예외가 발생하였습니다.({})", file.getName(), e);
//			}
//		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(TorrentSupervisoryControlActionImpl.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
