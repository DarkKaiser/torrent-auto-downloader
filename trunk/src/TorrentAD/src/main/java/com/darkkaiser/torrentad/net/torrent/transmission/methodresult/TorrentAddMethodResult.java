package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public final class TorrentAddMethodResult extends AbstractMethodResult {

	public final class Argument {
		
		@SerializedName("torrent-added")
		public TorreatAdded torrentAdded;

		@SerializedName("torrent-duplicate")
		public TorreatAdded torrentDuplicate;

	}

	@Getter
	public final class TorreatAdded {
				
		private long id;
		private String name;
		private String hashString;

	}

	public Argument arguments;
	
}
