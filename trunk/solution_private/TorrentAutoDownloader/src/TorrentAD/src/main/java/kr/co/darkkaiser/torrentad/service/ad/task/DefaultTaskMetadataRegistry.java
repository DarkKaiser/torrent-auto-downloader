package kr.co.darkkaiser.torrentad.service.ad.task;

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

public final class DefaultTaskMetadataRegistry implements TaskMetadataRegistry {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTaskMetadataRegistry.class);

	private String filePath = null;

	private Properties properties = new SortedProperties();

	public DefaultTaskMetadataRegistry(String filePath) {
		if (filePath == null) {
			throw new NullPointerException("filePath");
		}
		if (StringUtil.isBlank(filePath) == true) {
			throw new IllegalArgumentException("filePath는 빈 문자열을 허용하지 않습니다.");
		}

		this.filePath = filePath;

		try {
			initialize();
		} catch (IOException e) {
			logger.error(null, e);
		}
	}

	private void initialize() throws IOException {
		assert this.filePath != null;
		assert this.properties != null;

		try {
			try (FileInputStream fis = new FileInputStream(new File(this.filePath))) {
				this.properties.load(fis);
			}
		} catch (FileNotFoundException e) {
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

		int value = 0;

		try {
			value = Integer.parseInt(this.properties.getProperty(key));
		} catch (Exception ex) {
			return defaultValue;
		}

		return value;
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

		long value = 0;

		try {
			value = Long.parseLong(this.properties.getProperty(key));
		} catch (Exception ex) {
			return defaultValue;
		}

		return value;
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

		if (value == null) {
			return defaultValue;
		}

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

		double value = 0.0;

		try {
			value = Double.valueOf(this.properties.getProperty(key)).doubleValue();
		} catch (Exception ex) {
			return defaultValue;
		}

		return value;
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

		boolean value = false;

		try {
			value = Boolean.valueOf(this.properties.getProperty(key)).booleanValue();
		} catch (Exception ex) {
			return defaultValue;
		}

		return value;
	}

	@Override
	public synchronized void setBoolean(String key, boolean value) {
		assert this.properties != null;
		this.properties.setProperty(key, Boolean.toString(value));

		store();
	}

	public boolean exists() {
		try {
			return new File(this.filePath).exists();
		} catch (Exception e) {
		}

		return false;
	}

}
