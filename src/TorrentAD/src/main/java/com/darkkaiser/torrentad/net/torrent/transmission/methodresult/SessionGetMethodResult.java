package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import com.google.gson.annotations.SerializedName;

public final class SessionGetMethodResult extends AbstractMethodResult {

	public static final class Argument {

		@SerializedName("rpc-version")
		private long rpcVersion;

		@SerializedName("download-dir")
		private String downloadDir;

		private String version;

		public long getRpcVersion() {
			return this.rpcVersion;
		}

		public String getDownloadDir() {
			return this.downloadDir;
		}

		public String getVersion() {
			return this.version;
		}

	}
	
	public Argument arguments;

}
