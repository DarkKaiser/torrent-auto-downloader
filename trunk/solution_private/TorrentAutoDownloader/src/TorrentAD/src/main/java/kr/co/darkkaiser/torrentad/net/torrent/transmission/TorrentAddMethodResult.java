package kr.co.darkkaiser.torrentad.net.torrent.transmission;

import com.google.gson.annotations.SerializedName;

import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.AbstractMethodResult;

// @@@@@
public final class TorrentAddMethodResult extends AbstractMethodResult {

	private final class Argument {
		
		@SerializedName("torrent-added")
		private TorreatAdded torrentadded;

		public long getId() {
			return this.torrentadded.getId();
		}
		
		public String getName() {
			return this.torrentadded.getName();
		}

		public String getHashString() {
			return this.torrentadded.getHashString();
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

	// @@@@@ 배열 아닌가?
	private Argument arguments;
	
	public long getId() {
		return this.arguments.getId();
	}
	
	public String getName() {
		return this.arguments.getName();
	}
	
	public String getHashString() {
		return this.arguments.getHashString();
	}

}
