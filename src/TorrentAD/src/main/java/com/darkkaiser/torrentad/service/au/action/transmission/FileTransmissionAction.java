package com.darkkaiser.torrentad.service.au.action.transmission;

import java.io.File;

import com.darkkaiser.torrentad.service.au.action.Action;

public interface FileTransmissionAction extends Action {

	boolean addFile(final File file);

	int getFileCount();

}
