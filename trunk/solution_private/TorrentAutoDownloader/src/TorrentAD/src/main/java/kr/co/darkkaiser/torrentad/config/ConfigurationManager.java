package kr.co.darkkaiser.torrentad.config;

public interface ConfigurationManager {

	String getValue(String key);

	String getValue(String key, String defaultValue);

	String getPath();

	void dispose();

}
