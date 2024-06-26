package com.darkkaiser.torrentad.util;

public final class RadixNotation62Util {

	private static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public static String toString(long value) {
		if (value < 0)
			return "";

		char[] charBuf = new char[33];
		int charBufPos = charBuf.length - 1;
		int radix = digits.length;

		while (true) {
			charBuf[charBufPos--] = digits[(int) (value % radix)];
			value = value / radix;
			if (value == 0)
				break;
		}

		return new String(charBuf, charBufPos + 1, (charBuf.length - 1 - charBufPos));
	}
	
	private RadixNotation62Util() {

	}

}
