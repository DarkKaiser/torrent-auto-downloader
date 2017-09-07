package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

public abstract class AbstractMethodResult implements MethodResult {

	private long tag;

	private String result;

	@Override
	public long getTag() {
		return this.tag;
	}

	@Override
	public String getResult() {
		return this.result;
	}

	@Override
	public boolean isResultSuccess() {
		return this.result.equals("success");
	}

}
