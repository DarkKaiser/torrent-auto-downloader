package kr.co.darkkaiser.torrentad.service.supervisorycontrol.action;

import java.io.File;

public interface FileTransmissionAction extends Action {

	boolean addFile(File file);

	int getFileCount();

}
