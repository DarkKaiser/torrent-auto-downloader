package com.darkkaiser.torrentad.net.torrent;

import java.io.File;
import java.util.List;

import com.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;

public interface TorrentClient {

	boolean connect(final String user, final String password) throws Exception;

	void disconnect() throws Exception;

	boolean isConnected();

	boolean addTorrent(final File file, final boolean paused) throws Exception;

	boolean startTorrent(final List<Long> ids) throws Exception;

	TorrentGetMethodResult getTorrent() throws Exception;

}
