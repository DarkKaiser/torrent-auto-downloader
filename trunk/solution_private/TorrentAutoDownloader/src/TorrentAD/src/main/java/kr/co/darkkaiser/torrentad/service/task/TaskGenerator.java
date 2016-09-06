package kr.co.darkkaiser.torrentad.service.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeyword;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordType;

public class TaskGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskGenerator.class);
	
	private TaskGenerator() {
	}

	public static List<Task> generate(ConfigurationManager configurationManager, WebSite site) throws Exception {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		if (site == null) {
			throw new NullPointerException("site");
		}
		
		List<Task> tasks = new ArrayList<>();

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new File(configurationManager.getFilePath()));
			doc.getDocumentElement().normalize();

			String nodeName = null;
			NodeList cvNodeList = doc.getElementsByTagName(Constants.APP_CONFIG_TAG_PERIODIC_TASK);

			for (int cvNodeListIndex = 0; cvNodeListIndex < cvNodeList.getLength(); ++cvNodeListIndex) {
				Node cvNode = cvNodeList.item(cvNodeListIndex);

				if (cvNode.getNodeType() == Node.ELEMENT_NODE) {
					Task task = TaskFactory.newInstance(TaskType.PERIODIC, site);

					NodeList cvChildNodeList = cvNode.getChildNodes();
					for (int cvChildNodeListIndex = 0; cvChildNodeListIndex < cvChildNodeList.getLength(); ++cvChildNodeListIndex) {
						Node cvChildNode = cvChildNodeList.item(cvChildNodeListIndex);

						if (cvChildNode.getNodeType() == Node.ELEMENT_NODE) {
							nodeName = cvChildNode.getNodeName();

							if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_TASK_BOARD_NAME) == true) {
								task.setBoardName(cvChildNode.getTextContent().trim());
							} else if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS) == true) {
								NodeList cvSearchKeywordNodeList = cvChildNode.getChildNodes();

								for (int cvSearchKeywordNodeListIndex = 0; cvSearchKeywordNodeListIndex < cvSearchKeywordNodeList.getLength(); ++cvSearchKeywordNodeListIndex) {
									Node cvSearchKeywordNode = cvSearchKeywordNodeList.item(cvSearchKeywordNodeListIndex);

									if (cvSearchKeywordNode.getNodeType() == Node.ELEMENT_NODE) {
										String searchKeywordType = WebSiteSearchKeywordType.INCLUDE.getValue();
										if (cvSearchKeywordNode.getAttributes().getNamedItem("type") != null) {
											searchKeywordType = cvSearchKeywordNode.getAttributes().getNamedItem("type").getNodeValue();
										}

										WebSiteSearchKeyword searchKeyword = site.createSearchKeyword(searchKeywordType);

										Node cvSearchKeywordChildNode = cvSearchKeywordNode.getFirstChild();
										while (cvSearchKeywordChildNode != null) {
											if (cvSearchKeywordChildNode.getNodeType() == Node.ELEMENT_NODE) {
												if (cvSearchKeywordChildNode.getNodeName().equals("item") == true) {
													searchKeyword.add(cvSearchKeywordChildNode.getTextContent().trim());
												}
											}

											cvSearchKeywordChildNode = cvSearchKeywordChildNode.getNextSibling();											
										}

										if (searchKeyword.isValid() == true) {
											task.add(searchKeyword);
										} else {
											throw new XMLParseException("SearchKeyword 정보가 유효하지 않습니다.");
										}
									}
								}
							} else {
								logger.warn("유효하지 않은 XML 항목:{}={}", cvChildNode.getNodeName(), cvChildNode.getTextContent());
								assert false;
							}
						}
					}

					if (task.isValid() == true) {
						logger.debug("Task 생성완료:{}", task.toString());
						tasks.add(task);
					} else {
						throw new XMLParseException(task.toString());
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("환경설정정보 파일을 찾을 수 없습니다.(경로:'{}')", configurationManager.getFilePath());
			throw e;
		} catch (Exception e) {
			logger.error("환경설정정보를 읽어들이는 중에 예외가 발생하였습니다.");
			throw e;
		} finally {
		}

		return tasks;
	}

}
