package kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import java.util.List;

public final class TorrentGetMethodResult extends AbstractMethodResult {

	public final class Argument {

		public List<Torrent> torrents;

	}

	public final class Torrent {

		private long id;
		private long status;
		private boolean isStalled;
		private boolean isFinished;
		private long error;
		private String errorString;

		public long getId() {
			return this.id;
		}
		
		public long status() {
			return this.status;
		}
		
		public boolean isStalled() {
			return this.isStalled;
		}
		
		public boolean isFinished() {
			return this.isFinished;
		}
		
		public long error() {
			return this.error;
		}
		
		public String getErrorString() {
			return this.errorString;
		}
		
	}

	public Argument arguments;
	
}
