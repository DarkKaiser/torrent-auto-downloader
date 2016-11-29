package kr.co.darkkaiser.torrentad.util;

public final class OutParam<T> {
	
	private T result;

	public void set(T result) {
		this.result = result;
	}

	public T get() {
		return this.result;
	}

	@Override
	public String toString() {
		return this.result.toString();
	}

}
