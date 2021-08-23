package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public final class SessionGetMethodResult extends AbstractMethodResult {

	@Getter
	public final class Argument {

		@SerializedName("rpc-version")
		private long rpcVersion;

		@SerializedName("download-dir")
		private String downloadDir;

		private String version;
		
	}
	
	public Argument arguments;

}
