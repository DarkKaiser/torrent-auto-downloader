package com.darkkaiser.torrentad.config;

import com.darkkaiser.torrentad.util.Disposable;

public interface Configuration extends Disposable {

	String getFilePath();

	String getValue(final String key);

	String getValue(final String key, final String defaultValue);

}
