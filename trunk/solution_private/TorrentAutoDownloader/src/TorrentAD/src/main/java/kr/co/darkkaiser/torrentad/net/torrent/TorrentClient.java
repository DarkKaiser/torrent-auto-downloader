package kr.co.darkkaiser.torrentad.net.torrent;

import java.io.File;
import java.io.IOException;

public interface TorrentClient {

	boolean connect(final String user, final String password) throws Exception;

	void disconnect() throws Exception;

	boolean isConnected();

	boolean addTorrent(File file, boolean paused) throws Exception;

	//@@@@@
	void get() throws IOException;

}
