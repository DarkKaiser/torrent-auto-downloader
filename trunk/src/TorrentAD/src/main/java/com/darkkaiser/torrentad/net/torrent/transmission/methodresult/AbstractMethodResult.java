package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import lombok.Getter;

@Getter
public abstract class AbstractMethodResult implements MethodResult {

	private long tag;

	private String result;

	@Override
	public boolean isResultSuccess() {
		return this.result.equals("success");
	}

}
