package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.filetransmission;

import java.io.File;

import kr.co.darkkaiser.torrentad.service.supervisorycontrol.action.Action;

public interface FileTransmissionAction extends Action {

	boolean addFile(File file);

	int getFileCount();

}
