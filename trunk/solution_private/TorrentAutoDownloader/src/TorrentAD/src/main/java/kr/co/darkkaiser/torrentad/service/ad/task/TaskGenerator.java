package kr.co.darkkaiser.torrentad.service.ad.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteConstants;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsMode;
import kr.co.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;

public final class TaskGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskGenerator.class);
	
	private TaskGenerator() {
	}

	public static List<Task> generate(Configuration configuration, WebSite site) throws Exception {
		if (configuration == null) {
			throw new NullPointerException("configuration");
		}
		if (site == null) {
			throw new NullPointerException("site");
		}
		
		List<Task> tasks = new ArrayList<>();

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new File(configuration.getFilePath()));
			doc.getDocumentElement().normalize();

			String nodeName = null;
			NodeList cvNodeList = doc.getElementsByTagName(Constants.APP_CONFIG_TAG_PERIODIC_TASK);

			for (int cvNodeListIndex = 0; cvNodeListIndex < cvNodeList.getLength(); ++cvNodeListIndex) {
				Node cvNode = cvNodeList.item(cvNodeListIndex);

				if (cvNode.getNodeType() == Node.ELEMENT_NODE) {
					Task task = TaskFactory.createTask(TaskType.PERIODIC, cvNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_TASK_ATTR_ID).getNodeValue(), site);

					NodeList cvChildNodeList = cvNode.getChildNodes();
					for (int cvChildNodeListIndex = 0; cvChildNodeListIndex < cvChildNodeList.getLength(); ++cvChildNodeListIndex) {
						Node cvChildNode = cvChildNodeList.item(cvChildNodeListIndex);

						if (cvChildNode.getNodeType() == Node.ELEMENT_NODE) {
							nodeName = cvChildNode.getNodeName();

							if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_TASK_BOARD_NAME) == true) {
								task.setBoardName(cvChildNode.getTextContent().trim());
							} else if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_TASK_LATEST_DOWNLOAD_BOARD_ITEM_IDENTIFIER) == true) {
								// @@@@@ 프로퍼티 파일로 변경
								String identifier = cvChildNode.getTextContent().trim();
								if (StringUtil.isBlank(identifier) == true) {
									task.setLatestDownloadBoardItemIdentifier(WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE);
								} else {
									task.setLatestDownloadBoardItemIdentifier(Long.parseLong(identifier));
								}
							} else if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS) == true) {
								String searchKeywordsTypeString = cvChildNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORDS_ATTR_TYPE).getNodeValue();
								WebSiteSearchKeywordsType searchKeywordsType = WebSiteSearchKeywordsType.fromString(searchKeywordsTypeString);

								NodeList cvSearchKeywordNodeList = cvChildNode.getChildNodes();
								for (int cvSearchKeywordNodeListIndex = 0; cvSearchKeywordNodeListIndex < cvSearchKeywordNodeList.getLength(); ++cvSearchKeywordNodeListIndex) {
									Node cvSearchKeywordNode = cvSearchKeywordNodeList.item(cvSearchKeywordNodeListIndex);

									if (cvSearchKeywordNode.getNodeType() == Node.ELEMENT_NODE) {
										String searchKeywordsMode = WebSiteSearchKeywordsMode.getDefault().getValue();
										if (cvSearchKeywordNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_ATTR_MODE) != null) {
											searchKeywordsMode = cvSearchKeywordNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_ATTR_MODE).getNodeValue();
										}

										WebSiteSearchKeywords searchKeywords = site.createSearchKeywords(searchKeywordsMode);

										Node cvSearchKeywordChildNode = cvSearchKeywordNode.getFirstChild();
										while (cvSearchKeywordChildNode != null) {
											if (cvSearchKeywordChildNode.getNodeType() == Node.ELEMENT_NODE) {
												if (cvSearchKeywordChildNode.getNodeName().equals(Constants.APP_CONFIG_TAG_PERIODIC_TASK_SEARCH_KEYWORD_ITEM) == true) {
													searchKeywords.add(cvSearchKeywordChildNode.getTextContent().trim());
												}
											}

											cvSearchKeywordChildNode = cvSearchKeywordChildNode.getNextSibling();											
										}

										if (searchKeywords.isValid() == true) {
											task.addSearchKeywords(searchKeywordsType, searchKeywords);
										} else {
											throw new XMLParseException("생성된 SearchKeywords가 유효하지 않습니다.");
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
			logger.error("프로그램 설정정보 파일을 찾을 수 없습니다.(경로:'{}')", configuration.getFilePath());
			throw e;
		} catch (Exception e) {
			logger.error("프로그램 설정정보를 읽어들이는 중에 예외가 발생하였습니다.");
			throw e;
		} finally {
		}

		return tasks;
	}

}
