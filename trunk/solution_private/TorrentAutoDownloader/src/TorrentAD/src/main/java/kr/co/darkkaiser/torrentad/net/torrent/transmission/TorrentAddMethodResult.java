package kr.co.darkkaiser.torrentad.net.torrent.transmission;

import com.google.gson.annotations.SerializedName;

import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.AbstractMethodResult;

public final class TorrentAddMethodResult extends AbstractMethodResult {
	private final class TorreadAddMethodResultArgument2 {
		private String hashString;
		private long id;
		private String name;
	}
	
	private final class TorreadAddMethodResultArgument {
		@SerializedName("torrent-added")
		private TorreadAddMethodResultArgument2 torrentadded = new TorreadAddMethodResultArgument2();
	}
	
	private TorreadAddMethodResultArgument arguments;

	// @@@@@
//	{"arguments":{"torrent-added":{"hashString":"01c95c6e645279cac028e2316584b425249604d3","id":5,"name":"\ud0a5\ubcf5\uc11c (Kickboxer, 2016) 1080p WEB-DL"}},
//	"result":"success"}

}
