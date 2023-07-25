package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import com.google.gson.annotations.SerializedName;

public final class TorrentAddMethodResult extends AbstractMethodResult {

	public final class Argument {
		
		@SerializedName("torrent-added")
		public TorreatAdded torrentAdded;

		@SerializedName("torrent-duplicate")
		public TorreatAdded torrentDuplicate;

	}

	public static final class TorreatAdded {
				
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
