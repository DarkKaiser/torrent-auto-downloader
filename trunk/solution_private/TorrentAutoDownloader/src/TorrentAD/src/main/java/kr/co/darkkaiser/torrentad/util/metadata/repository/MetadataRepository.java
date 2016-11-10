package kr.co.darkkaiser.torrentad.util.metadata.repository;

public interface MetadataRepository {

	int getInt(String key, int defaultValue);

	void setInt(String key, int value);

	long getLong(String key, long defaultValue);

	void setLong(String key, long value);

	String getString(String key, String defaultValue);

	void setString(String key, String value);

	double getDouble(String key, double defaultValue);

	void setDouble(String key, double value);

	boolean getBoolean(String key, boolean defaultValue);

	void setBoolean(String key, boolean value);

}
