package kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import com.google.gson.annotations.SerializedName;

public final class SessionGetMethodResult extends AbstractMethodResult {

	private final class Argument {

		@SerializedName("rpc-version")
		private long rpcVersion;

		@SerializedName("download-dir")
		private String downloadDir;

		private String version;
		
		@SuppressWarnings("unused")
		public long getRpcVersion() {
			return this.rpcVersion;
		}
		
		@SuppressWarnings("unused")
		public String getDownloadDir() {
			return this.downloadDir;
		}
		
		@SuppressWarnings("unused")
		public String getVersion() {
			return this.version;
		}

	}
	
	public Argument arguments;

}
