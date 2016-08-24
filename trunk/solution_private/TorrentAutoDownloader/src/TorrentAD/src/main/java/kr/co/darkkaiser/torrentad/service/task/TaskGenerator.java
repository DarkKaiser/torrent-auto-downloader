package kr.co.darkkaiser.torrentad.service.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;

public class TaskGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskGenerator.class);
	
	private TaskGenerator() {
	}

	public static void load(ConfigurationManager configurationManager, List<Task> tasks) {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}
		if (tasks == null) {
			throw new NullPointerException("tasks");
		}
		
		/////////////////////////////////////////////////////////////////
		
		// @@@@@
		//@@@@@ 환경설정정보 로드해서 task 초기화

		tasks.add(new PeriodicTaskImpl());
		
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new File(configurationManager.getFilePath()));
			doc.getDocumentElement().normalize();

//			String port = null;
			String nodeName = null;
//			String serverType = null;
			NodeList cvNodeList = doc.getElementsByTagName("torrentad-periodic-task");

			for (int cvNodeListIndex = 0; cvNodeListIndex < cvNodeList.getLength(); ++cvNodeListIndex) {
				Node cvNode = cvNodeList.item(cvNodeListIndex);

				if (cvNode.getNodeType() == Node.ELEMENT_NODE) {
//					Server server = new Server();

					NodeList cvChildNodeList = cvNode.getChildNodes();
					for (int cvChildNodeListIndex = 0; cvChildNodeListIndex < cvChildNodeList.getLength(); ++cvChildNodeListIndex) {
						Node cvChildNode = cvChildNodeList.item(cvChildNodeListIndex);

						if (cvChildNode.getNodeType() == Node.ELEMENT_NODE) {
							nodeName = cvChildNode.getNodeName();

//							if (nodeName.equals("id") == true) {
//								server.setServerId(cvChildNode.getTextContent());
//							} else if (nodeName.equals("description") == true) {
//								server.setServerDescription(cvChildNode.getTextContent());
//							} else if (nodeName.equals("host") == true) {
//								server.setServerAddress(cvChildNode.getTextContent());
//							} else if (nodeName.equals("loadbalance_port") == true) {
//								port = cvChildNode.getTextContent().trim();
//
//								try {
//									server.setLoadBalancePort(Integer.parseInt(port));
//								} catch (NumberFormatException e) {
//									throw new NumberFormatException("유효하지 않은 LoadBalance 포트번호('" + port + "')입니다.");
//								}
//							} else if (cvChildNode.getNodeName().equals("matchers") == true) {
//								NodeList cvMatcherChildNodeList = cvChildNode.getChildNodes();
//
//								for (int cvMatcherChildNodeListIndex = 0; cvMatcherChildNodeListIndex < cvMatcherChildNodeList.getLength(); ++cvMatcherChildNodeListIndex) {
//									Node cvMatcherChildNode = cvMatcherChildNodeList.item(cvMatcherChildNodeListIndex);
//									
//									if (cvMatcherChildNode.getNodeType() == Node.ELEMENT_NODE) {
//										// 서비스되는 matcher에 대한 정보를 읽어들인다.
//										ServerType matcherServerType = null;
//										int matcherServerPort = Constants.INVALID_PORT_NUMBER;
//										Node cvMatcherChildChildNode = cvMatcherChildNode.getFirstChild();
//
//										while (cvMatcherChildChildNode != null) {
//											if (cvMatcherChildChildNode.getNodeType() == Node.ELEMENT_NODE) {
//												if (cvMatcherChildChildNode.getNodeName().equals("type") == true) {
//													serverType = cvMatcherChildChildNode.getTextContent().trim();
//													
//													try {
//														matcherServerType = ServerType.valueOf(serverType);
//														if (matcherServerType == ServerType.UNKNOWN)
//															matcherServerType = null;														
//													} catch (IllegalArgumentException e) {
//														throw new IllegalArgumentException("유효하지 않은 Matcher 서버 타입('" + serverType + "')입니다.");
//													}
//												} else if (cvMatcherChildChildNode.getNodeName().equals("port") == true) {
//													port = cvMatcherChildChildNode.getTextContent().trim();
//													
//													try {
//														matcherServerPort = Integer.parseInt(port);
//													} catch (NumberFormatException e) {
//														throw new NumberFormatException("유효하지 않은 Matcher 서버 포트번호('" + port + "')입니다.");
//													}
//												}
//											}
//
//											cvMatcherChildChildNode = cvMatcherChildChildNode.getNextSibling();											
//										}
//
//										if (matcherServerType != null && matcherServerPort != Constants.INVALID_PORT_NUMBER) {
//											server.addMatcher(matcherServerType, matcherServerPort);
//										} else {
//											throw new XMLParseException(server.getServerId() + " 서버의 matcher 정보가 올바르지 않습니다.");
//										}
//									}
//								}
//							} else {
//								logger.warn("유효하지 않은 XML 항목: {} = {}", cvChildNode.getNodeName(), cvChildNode.getTextContent());
//								assert false;
//							}
						}
					}

//					if (server.isValid() == true) {
//						if (serverList.containsKey(server.getServerId()) == true) {
//							throw new AlreadyRegistrationException("ServerID:" + server.getServerId());
//						}
//
//						logger.debug(server.toString());
//						serverList.put(server.getServerId(), server);
//					} else {
//						throw new XMLParseException(server.toString());
//					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("환경설정정보 파일을 찾을 수 없습니다.(파일경로 : '{}')", configurationManager.getFilePath(), e);
//			return false;
		} catch (Exception e) {
			logger.error("환경설정정보를 읽어들이는 중에 예외가 발생하였습니다.", e);
//			return false;
		} finally {
		}

	}

}
