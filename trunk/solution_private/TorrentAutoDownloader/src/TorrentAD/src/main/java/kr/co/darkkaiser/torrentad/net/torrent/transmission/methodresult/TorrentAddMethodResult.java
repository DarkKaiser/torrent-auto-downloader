package kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import com.google.gson.annotations.SerializedName;

public final class TorrentAddMethodResult extends AbstractMethodResult {

	private final class Argument {
		
		@SerializedName("torrent-added")
		private TorreatAdded torrentAdded;

		@SuppressWarnings("unused")
		public long getId() {
			return this.torrentAdded.getId();
		}
		
		@SuppressWarnings("unused")
		public String getName() {
			return this.torrentAdded.getName();
		}

		@SuppressWarnings("unused")
		public String getHashString() {
			return this.torrentAdded.getHashString();
		}

	}
	
	private final class TorreatAdded {
				
		private long id;
		private String name;
		private String hashString;

		public long getId() {
			return this.id;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getHashString() {
			return this.hashString;
		}		
	
	}

	public Argument arguments;
	
}
