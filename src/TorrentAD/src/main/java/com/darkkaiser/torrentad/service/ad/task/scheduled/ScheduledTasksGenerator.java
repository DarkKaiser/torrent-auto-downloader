package com.darkkaiser.torrentad.service.ad.task.scheduled;

import com.darkkaiser.torrentad.common.Constants;
import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.service.ad.task.TaskFactory;
import com.darkkaiser.torrentad.service.ad.task.TaskType;
import com.darkkaiser.torrentad.util.metadata.repository.MetadataRepository;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywords;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywordsMode;
import com.darkkaiser.torrentad.website.WebSiteSearchKeywordsType;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class ScheduledTasksGenerator {
	
	private ScheduledTasksGenerator() {

	}

	public static List<ScheduledTask> generate(final Configuration configuration, final MetadataRepository metadataRepository, final WebSite site) throws Exception {
		Objects.requireNonNull(configuration, "configuration");
		Objects.requireNonNull(metadataRepository, "metadataRepository");
		Objects.requireNonNull(site, "site");

		List<ScheduledTask> tasks = new ArrayList<>();

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new File(configuration.getFilePath()));
			doc.getDocumentElement().normalize();

			String nodeName;
			NodeList cvNodeList = doc.getElementsByTagName(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK);

			for (int cvNodeListIndex = 0; cvNodeListIndex < cvNodeList.getLength(); ++cvNodeListIndex) {
				Node cvNode = cvNodeList.item(cvNodeListIndex);

				if (cvNode.getNodeType() == Node.ELEMENT_NODE) {
					String taskId = cvNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_TASK_ATTR_ID).getNodeValue();
					String taskDescription = cvNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_TASK_ATTR_DESCRIPTION).getNodeValue();
					ScheduledTask task = ((ScheduledTask) TaskFactory.createTask(TaskType.PERIODIC_SCHEDULED, taskId, taskDescription, metadataRepository))
							.setWebSite(site);

					NodeList cvChildNodeList = cvNode.getChildNodes();
					for (int cvChildNodeListIndex = 0; cvChildNodeListIndex < cvChildNodeList.getLength(); ++cvChildNodeListIndex) {
						Node cvChildNode = cvChildNodeList.item(cvChildNodeListIndex);

						if (cvChildNode.getNodeType() == Node.ELEMENT_NODE) {
							nodeName = cvChildNode.getNodeName();

							if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_BOARD_NAME) == true) {
								task.setBoardName(cvChildNode.getTextContent().trim());
							} else if (nodeName.equals(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORDS) == true) {
								String searchKeywordsTypeString = cvChildNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORDS_ATTR_TYPE).getNodeValue();
								WebSiteSearchKeywordsType searchKeywordsType = WebSiteSearchKeywordsType.fromString(searchKeywordsTypeString);

								NodeList cvSearchKeywordNodeList = cvChildNode.getChildNodes();
								for (int cvSearchKeywordNodeListIndex = 0; cvSearchKeywordNodeListIndex < cvSearchKeywordNodeList.getLength(); ++cvSearchKeywordNodeListIndex) {
									Node cvSearchKeywordNode = cvSearchKeywordNodeList.item(cvSearchKeywordNodeListIndex);

									if (cvSearchKeywordNode.getNodeType() == Node.ELEMENT_NODE) {
										String searchKeywordsMode = WebSiteSearchKeywordsMode.getDefault().getValue();
										if (cvSearchKeywordNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORD_ATTR_MODE) != null) {
											searchKeywordsMode = cvSearchKeywordNode.getAttributes().getNamedItem(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORD_ATTR_MODE).getNodeValue();
										}

										WebSiteSearchKeywords searchKeywords = site.createSearchKeywords(searchKeywordsMode);

										Node cvSearchKeywordChildNode = cvSearchKeywordNode.getFirstChild();
										while (cvSearchKeywordChildNode != null) {
											if (cvSearchKeywordChildNode.getNodeType() == Node.ELEMENT_NODE) {
												if (cvSearchKeywordChildNode.getNodeName().equals(Constants.APP_CONFIG_TAG_PERIODIC_SCHEDULED_TASK_SEARCH_KEYWORD_ITEM) == true) {
													searchKeywords.add(cvSearchKeywordChildNode.getTextContent().trim());
												}
											}

											cvSearchKeywordChildNode = cvSearchKeywordChildNode.getNextSibling();											
										}

										if (searchKeywords.isValid() == true)
											task.addSearchKeywords(searchKeywordsType, searchKeywords);
										else
											throw new XMLParseException(String.format("생성된 SearchKeywords가 유효하지 않습니다.(Task:%s)", taskId));
									}
								}
							} else {
								log.warn("유효하지 않은 XML 항목:{}={}", cvChildNode.getNodeName(), cvChildNode.getTextContent());
								assert false;
							}
						}
					}

					if (task.isValid() == true) {
						tasks.add(task);
						log.debug("Task 생성완료:{}", task);
					} else {
						throw new XMLParseException(task.toString());
					}
				}
			}
		} catch (final FileNotFoundException e) {
			log.error("프로그램 설정정보 파일을 찾을 수 없습니다.(경로:'{}')", configuration.getFilePath());
			throw e;
		} catch (final Exception e) {
			log.error("프로그램 설정정보를 읽어들이는 중에 예외가 발생하였습니다.");
			throw e;
		}

		return tasks;
	}

}
