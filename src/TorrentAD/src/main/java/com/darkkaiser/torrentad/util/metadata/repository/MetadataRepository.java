package com.darkkaiser.torrentad.util.metadata.repository;

@SuppressWarnings("unused")
public interface MetadataRepository {

	int getInt(final String key, final int defaultValue);

	void setInt(final String key, final int value);

	long getLong(final String key, final long defaultValue);

	void setLong(final String key, final long value);

	String getString(final String key, final String defaultValue);

	void setString(final String key, final String value);

	double getDouble(final String key, final double defaultValue);

	void setDouble(final String key, final double value);

	boolean getBoolean(final String key, final boolean defaultValue);

	void setBoolean(final String key, final boolean value);

}
