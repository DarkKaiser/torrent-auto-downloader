package kr.co.darkkaiser.torrentad.config;

import kr.co.darkkaiser.torrentad.util.Disposable;

public interface Configuration extends Disposable {

	String getFilePath();

	String getValue(String key);

	String getValue(String key, String defaultValue);

}
