package kr.co.darkkaiser.torrentad.service.supervisorycontrol.transmitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public abstract class AbstractFileTransmitter implements FileTransmitter {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractFileTransmitter.class);
	
	protected final Configuration configuration;

	private AES256Util aes256;

	protected AbstractFileTransmitter(Configuration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
	}

	protected String decode(String encryption) throws Exception {
		if (this.aes256 == null)
			this.aes256 = new AES256Util();

		try {
			return this.aes256.decode(encryption);
		} catch (Exception e) {
			logger.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", encryption);
			throw e;
		}
	}

}
