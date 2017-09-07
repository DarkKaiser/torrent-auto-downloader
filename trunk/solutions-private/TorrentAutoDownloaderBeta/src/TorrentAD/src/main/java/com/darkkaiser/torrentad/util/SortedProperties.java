package com.darkkaiser.torrentad.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {

	private static final long serialVersionUID = -3870119338315926034L;

	public synchronized Enumeration<Object> keys() {
		Enumeration<Object> keysEnum = super.keys();
		Vector<Object> keyList = new Vector<Object>();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}

		Collections.sort(keyList, new Comparator<Object>() {
			@Override
			public int compare(Object lhs, Object rhs) {
				return lhs.toString().compareTo(rhs.toString());
			}
		});

		return keyList.elements();
	}

}
