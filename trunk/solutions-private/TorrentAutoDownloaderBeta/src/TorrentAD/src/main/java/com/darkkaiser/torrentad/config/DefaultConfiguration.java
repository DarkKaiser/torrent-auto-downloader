package com.darkkaiser.torrentad.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.darkkaiser.torrentad.common.Constants;

public final class DefaultConfiguration implements Configuration {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

	private String filePath;
	
	private final Hashtable<String/* 키 */, String/* 값 */> configValues = new Hashtable<>();
	
	public DefaultConfiguration() throws Exception {
		load(Constants.APP_CONFIG_FILE_NAME);
	}

	private void load(final String filePath) throws Exception {
		if (filePath == null)
			throw new NullPointerException("filePath");
		if (StringUtil.isBlank(filePath) == true)
			throw new IllegalArgumentException("filePath는 빈 문자열을 허용하지 않습니다.");

		synchronized (this.configValues) {
			this.filePath = null;
			this.configValues.clear();

			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

				Document doc = docBuilder.parse(new File(filePath));
				doc.getDocumentElement().normalize();

				NodeList cvNodeList = doc.getElementsByTagName(Constants.APP_CONFIG_TAG_SERVICE_CONFIG_VALUES);
				for (int cvNodeListIndex = 0; cvNodeListIndex < cvNodeList.getLength(); ++cvNodeListIndex) {
					Node cvNode = cvNodeList.item(cvNodeListIndex);

					if (cvNode.getNodeType() == Node.ELEMENT_NODE) {
						NodeList cvChildNodeList = cvNode.getChildNodes();
						for (int cvChildNodeListIndex = 0; cvChildNodeListIndex < cvChildNodeList.getLength(); ++cvChildNodeListIndex) {
							Node cvChildNode = cvChildNodeList.item(cvChildNodeListIndex);
							if (cvChildNode.getNodeType() == Node.ELEMENT_NODE) {
								this.configValues.put(cvChildNode.getNodeName(), cvChildNode.getTextContent());
								logger.debug("프로그램 설정정보:{}={}", cvChildNode.getNodeName(), cvChildNode.getTextContent());
							}
						}
					}
				}

				this.filePath = filePath;
			} catch (final FileNotFoundException e) {
				logger.error("프로그램 설정정보 파일을 찾을 수 없습니다.(파일경로:'{}')", filePath, e);
				throw e;
			} catch (final Exception e) {
				this.configValues.clear();
				logger.error("프로그램 설정정보를 읽어들이는 중에 예외가 발생하였습니다.", e);
				throw e;
			}
		}
	}

	@Override
	public String getFilePath() {
		return this.filePath;
	}

	@Override
	public String getValue(final String key) {
		return getValue(key, "");
	}

	@Override
	public String getValue(final String key, final  String defaultValue) {
		assert key != null;
		assert key.length() > 0;

		try {
			synchronized (this.configValues) {
				if (this.configValues.get(key) != null) {
					return this.configValues.get(key);
				}
			}

			logger.error("존재하지 않는 항목 정보를 요청(항목이름 : {})", key);
		} catch (final Exception e) {
			logger.error("항목 정보를 구하는 중에 예외가 발생하였습니다.", e);
		}

		return defaultValue;
	}
	
	@Override
	public void dispose() {
		synchronized (this.configValues) {
			this.filePath = null;
			this.configValues.clear();
		}
	}

}
