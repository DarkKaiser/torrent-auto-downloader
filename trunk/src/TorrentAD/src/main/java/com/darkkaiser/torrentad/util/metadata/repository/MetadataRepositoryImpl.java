package com.darkkaiser.torrentad.util.metadata.repository;

import com.darkkaiser.torrentad.util.SortedProperties;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class MetadataRepositoryImpl implements MetadataRepository {

	private static final Logger logger = LoggerFactory.getLogger(MetadataRepositoryImpl.class);

	private final String filePath;

	private final Properties properties = new SortedProperties();

	public MetadataRepositoryImpl(final String filePath) {
		Objects.requireNonNull(filePath, "filePath");

		if (StringUtil.isBlank(filePath) == true)
			throw new IllegalArgumentException("filePath는 빈 문자열을 허용하지 않습니다.");

		this.filePath = filePath;

		try {
			initialize();
		} catch (final IOException e) {
			logger.error(null, e);
		}
	}

	private void initialize() throws IOException {
		assert this.filePath != null;
		assert this.properties != null;

		try (FileInputStream fis = new FileInputStream(new File(this.filePath))) {
			this.properties.load(fis);
		}
	}

	private void store() {
		assert this.filePath != null;

		try {
			try (FileOutputStream fos = new FileOutputStream(new File(this.filePath))) {
				this.properties.store(fos, null);
			}
		} catch (final IOException e) {
			logger.error(null, e);
		}
	}

	@Override
	public synchronized int getInt(final String key, final int defaultValue) {
		assert this.properties != null;

		try {
			return Integer.parseInt(this.properties.getProperty(key));
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	@Override
	public synchronized void setInt(final String key, final int value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Integer.toString(value));

		store();
	}
	
	@Override
	public long getLong(final String key, final long defaultValue) {
		assert this.properties != null;

		try {
			return Long.parseLong(this.properties.getProperty(key));
		} catch (final Exception e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setLong(final String key, final long value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Long.toString(value));

		store();
	}

	@Override
	public synchronized String getString(final String key, final String defaultValue) {
		assert this.properties != null;

		String value = this.properties.getProperty(key);
		if (value == null)
			return defaultValue;

		return value;
	}

	@Override
	public synchronized void setString(final String key, final String value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, value);

		store();
	}

	@Override
	public synchronized double getDouble(final String key, final double defaultValue) {
		assert this.properties != null;

		try {
			return Double.valueOf(this.properties.getProperty(key));
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	@Override
	public synchronized void setDouble(final String key, final double value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Double.toString(value));

		store();
	}

	@Override
	public synchronized boolean getBoolean(final String key, final boolean defaultValue) {
		assert this.properties != null;

		try {
			return Boolean.valueOf(this.properties.getProperty(key));
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	@Override
	public synchronized void setBoolean(final String key, final boolean value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Boolean.toString(value));

		store();
	}

}
