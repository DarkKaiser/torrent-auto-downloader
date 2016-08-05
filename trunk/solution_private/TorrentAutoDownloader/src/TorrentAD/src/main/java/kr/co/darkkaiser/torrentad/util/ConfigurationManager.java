package kr.co.darkkaiser.torrentad.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigurationManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

	// 프로그램 환경설정 정보 리스트
	private Hashtable<String/* 키 */, String/* 값 */> mRuntimeConfigValues = new Hashtable<String, String>();

	public ConfigurationManager(String configFilePath) throws Exception {
		load(configFilePath);
	}

	private void load(String configFilePath) throws Exception {
		if (configFilePath == null)
			throw new NullPointerException();
		if (configFilePath.length() == 0)
			throw new IllegalArgumentException("서버 운영에 필요한 환경설정정보 파일이 입력되지 않았습니다.");

		synchronized (mRuntimeConfigValues) {
			mRuntimeConfigValues.clear();

			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

				Document doc = docBuilder.parse(new File(configFilePath));
				doc.getDocumentElement().normalize();

				NodeList cvNodeList = doc.getElementsByTagName("dispatcher-config-values");
				for (int cvNodeListIndex = 0; cvNodeListIndex < cvNodeList.getLength(); ++cvNodeListIndex) {
					Node cvNode = cvNodeList.item(cvNodeListIndex);

					if (cvNode.getNodeType() == Node.ELEMENT_NODE) {
						NodeList cvChildNodeList = cvNode.getChildNodes();
						for (int cvChildNodeListIndex = 0; cvChildNodeListIndex < cvChildNodeList.getLength(); ++cvChildNodeListIndex) {
							Node cvChildNode = cvChildNodeList.item(cvChildNodeListIndex);
							if (cvChildNode.getNodeType() == Node.ELEMENT_NODE) {
								mRuntimeConfigValues.put(cvChildNode.getNodeName(), cvChildNode.getTextContent());
								logger.debug("환경설정정보: {} = {}", cvChildNode.getNodeName(), cvChildNode.getTextContent());
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				logger.error("환경설정정보 파일을 찾을 수 없습니다.(파일경로 : '{}')", configFilePath, e);
				throw e;
			} catch (Exception e) {
				mRuntimeConfigValues.clear();
				logger.error("환경설정정보를 읽어들이는 중에 예외가 발생하였습니다.", e);
				throw e;
			}
		}
	}

	public String getValue(String key) {
		return getValue(key, "");
	}

	public String getValue(String key, String defaultValue) {
		assert key != null;
		assert key.length() > 0;

		try {
			synchronized (mRuntimeConfigValues) {
				if (mRuntimeConfigValues.get(key) != null) {
					return mRuntimeConfigValues.get(key);
				}
			}

			logger.error("존재하지 않는 항목 정보를 요청(항목이름 : {})", key);
		} catch (Exception e) {
			logger.error("항목 정보를 구하는 중에 예외가 발생하였습니다.", e);
		}

		return defaultValue;
	}

	public void dispose() {
		synchronized (mRuntimeConfigValues) {
			mRuntimeConfigValues.clear();
		}
	}

}
