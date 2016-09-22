package kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import java.util.List;

public final class TorrentGetMethodResult extends AbstractMethodResult {

	public final class Argument {

		public List<Torrent> torrents;

	}

	public final class Torrent {

		private long id;
		private int status;		// 0:Stopped, 1:Check waiting, 2:Checking, 3:Download waiting, 4:Downloading, 5:Seed waiting, 6:Seeding
		private boolean isStalled;
		private boolean isFinished;
		private int error;
		private String errorString;

		public long getId() {
			return this.id;
		}
		
		public int status() {
			return this.status;
		}
		
		public boolean isStalled() {
			return this.isStalled;
		}
		
		public boolean isFinished() {
			return this.isFinished;
		}
		
		public int error() {
			return this.error;
		}
		
		public String getErrorString() {
			return this.errorString;
		}
		
	}

	public Argument arguments;
	
}
