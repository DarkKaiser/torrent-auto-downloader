package com.darkkaiser.torrentad.util;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {

	private static final long serialVersionUID = -3870119338315926034L;

	public synchronized Enumeration<Object> keys() {
		Enumeration<Object> keysEnum = super.keys();
		Vector<Object> keyList = new Vector<>();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}

        keyList.sort(Comparator.comparing(Object::toString));

		return keyList.elements();
	}

}
