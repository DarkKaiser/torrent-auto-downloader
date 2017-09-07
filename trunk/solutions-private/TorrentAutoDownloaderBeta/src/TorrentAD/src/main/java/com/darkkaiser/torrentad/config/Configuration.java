package com.darkkaiser.torrentad.config;

import com.darkkaiser.torrentad.util.Disposable;

public interface Configuration extends Disposable {

	String getFilePath();

	String getValue(String key);

	String getValue(String key, String defaultValue);

}
