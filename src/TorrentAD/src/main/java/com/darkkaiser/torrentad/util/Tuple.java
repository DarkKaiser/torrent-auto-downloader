package com.darkkaiser.torrentad.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Tuple<F, L> {

	private F x;
	private L y;

	public F first() {
		return this.x;
	}

	public L last() {
		return this.y;
	}

	@SuppressWarnings("unused")
	public void first(final F x) {
		this.x = x;
	}

	@SuppressWarnings("unused")
	public void last(final L y) {
		this.y = y;
	}

}
