package kr.co.darkkaiser.torrentad.util.metadata.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.util.SortedProperties;

public final class MetadataRepositoryImpl implements MetadataRepository {

	private static final Logger logger = LoggerFactory.getLogger(MetadataRepositoryImpl.class);

	private final String filePath;

	private final Properties properties = new SortedProperties();

	public MetadataRepositoryImpl(String filePath) {
		if (filePath == null)
			throw new NullPointerException("filePath");
		if (StringUtil.isBlank(filePath) == true)
			throw new IllegalArgumentException("filePath는 빈 문자열을 허용하지 않습니다.");

		this.filePath = filePath;

		try {
			initialize();
		} catch (IOException e) {
			logger.error(null, e);
		}
	}

	private void initialize() throws FileNotFoundException, IOException {
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
		} catch (IOException e) {
			logger.error(null, e);
		}
	}

	@Override
	public synchronized int getInt(String key, int defaultValue) {
		assert this.properties != null;

		try {
			return Integer.parseInt(this.properties.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Override
	public synchronized void setInt(String key, int value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Integer.toString(value));

		store();
	}
	
	@Override
	public long getLong(String key, long defaultValue) {
		assert this.properties != null;

		try {
			return Long.parseLong(this.properties.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setLong(String key, long value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Long.toString(value));

		store();
	}

	@Override
	public synchronized String getString(String key, String defaultValue) {
		assert this.properties != null;

		String value = this.properties.getProperty(key);

		if (value == null)
			return defaultValue;

		return value;
	}

	@Override
	public synchronized void setString(String key, String value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, value);

		store();
	}

	@Override
	public synchronized double getDouble(String key, double defaultValue) {
		assert this.properties != null;

		try {
			return Double.valueOf(this.properties.getProperty(key)).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Override
	public synchronized void setDouble(String key, double value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Double.toString(value));

		store();
	}

	@Override
	public synchronized boolean getBoolean(String key, boolean defaultValue) {
		assert this.properties != null;

		try {
			return Boolean.valueOf(this.properties.getProperty(key)).booleanValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Override
	public synchronized void setBoolean(String key, boolean value) {
		assert this.properties != null;
		
		this.properties.setProperty(key, Boolean.toString(value));

		store();
	}

}
