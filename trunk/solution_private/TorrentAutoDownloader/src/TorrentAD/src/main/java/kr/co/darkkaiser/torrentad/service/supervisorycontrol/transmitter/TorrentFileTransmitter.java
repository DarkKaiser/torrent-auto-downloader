package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.darkkaiser.torrentad.config.Configuration;
import nl.stil4m.transmission.api.TransmissionRpcClient;
import nl.stil4m.transmission.api.domain.AddTorrentInfo;
import nl.stil4m.transmission.api.domain.AddedTorrentInfo;
import nl.stil4m.transmission.api.domain.TorrentInfo;
import nl.stil4m.transmission.rpc.RpcClient;
import nl.stil4m.transmission.rpc.RpcConfiguration;
import nl.stil4m.transmission.rpc.RpcException;

public class TorrentFileTransmitter extends AbstractFileTransmitter {

	private static final Logger logger = LoggerFactory.getLogger(TorrentFileTransmitter.class);
	
	// @@@@@
//	private FTPClient ftpClient;
	private TransmissionRpcClient rpcClient;

	public TorrentFileTransmitter(Configuration configuration) {
		super(configuration);
	}

	@Override
	public void prepare() throws Exception {
		// @@@@@
//		if (this.ftpClient != null && this.ftpClient.isConnected() == true) 
//			return;
//
//		String host = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_HOST);
//		String port = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_PORT);
//		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_ACCOUNT_ID);
//		String password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_ACCOUNT_PASSWORD));
//
//		this.ftpClient = new FTPClient();
//		if (this.ftpClient.connect(host, Integer.parseInt(port), id, password) == false)
//			logger.warn(String.format("FTP 서버 접속이 실패하였습니다.(Host:%s, Port:%s, Id:%s)", host, port, id));
		
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        RpcConfiguration rpcConfiguration = new RpcConfiguration();
        
//        URI u = new URI("http", "darkkaiser:DreamWakuWaku78@", "darkkaiser.gonetis.com", 9091, "/transmission/rpc", "", "");
//        rpcConfiguration.setHost(u);
        rpcConfiguration.setHost(URI.create("http://darkkaiser.gonetis.com:9091/transmission/rpc"));
        RpcClient client = new RpcClient(rpcConfiguration, objectMapper);
        rpcClient = new TransmissionRpcClient(client);
	}

	@Override
	public boolean transmit(File file) throws Exception {
		//@@@@@ 토렌트 파일은 정지상태로 올리기
		//transmission rpc
		//https://github.com/stil4m/transmission-rpc-java
		//https://sourceforge.net/projects/transmission-rj/

		if (file == null)
			throw new NullPointerException("file");
//		if (this.ftpClient == null)
//			throw new NullPointerException("ftpClient");
//		if (this.ftpClient.isConnected() == false)
//			throw new IllegalStateException("FTP 서버에 연결되어 있지 않습니다.");

		assert file.isDirectory() == false;

		if (file.exists() == false) {
			throw new FileNotFoundException(file.getAbsolutePath());
		} else {
			AddTorrentInfo addTorrentInfo = new AddTorrentInfo();
	        addTorrentInfo.setFilename(file.getAbsolutePath());
	        addTorrentInfo.setPaused(true);
	        AddedTorrentInfo result = null;
			try {
				result = rpcClient.addTorrent(addTorrentInfo);
			} catch (RpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        TorrentInfo info = result.getTorrentAdded();
	        
//			String remotePath = this.configuration.getValue(Constants.APP_CONFIG_TAG_FTP_SERVER_UPLOAD_LOCATION);
//			if (remotePath.endsWith("/") == true) {
//				remotePath += file.getName();
//			} else {
//				remotePath += "/" + file.getName();
//			}
//
//			return this.ftpClient.upload(file.getAbsolutePath(), remotePath);
		}

		return true;
	}

	@Override
	public boolean transmitFinished() {
		// @@@@@
//		if (this.ftpClient != null) {
//			try {
//				this.ftpClient.disconnect();
//			} catch (Exception e) {
//				logger.error(null, e);
//			}
//
//			this.ftpClient = null;
//		}
//
//		return true;
//		
		return true;
	}

	@Override
	public boolean support(File file) {
		if (file == null)
			throw new NullPointerException("file");
		
		if (file.isDirectory() == true)
			return false;

		return file.getName().toLowerCase().endsWith(".torrent");
	}

}
