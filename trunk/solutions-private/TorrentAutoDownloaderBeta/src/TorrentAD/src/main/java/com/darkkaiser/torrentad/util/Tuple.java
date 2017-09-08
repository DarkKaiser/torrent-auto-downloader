package com.darkkaiser.torrentad.util;

public class Tuple<F, L> {

	private F x;
	private L y;

	public Tuple(F x, L y) {
		this.x = x;
		this.y = y;
	}

	public F first() {
		return this.x;
	}

	public L last() {
		return this.y;
	}

	@SuppressWarnings("unused")
	public void first(F x) {
		this.x = x;
	}

	@SuppressWarnings("unused")
	public void last(L y) {
		this.y = y;
	}

}
