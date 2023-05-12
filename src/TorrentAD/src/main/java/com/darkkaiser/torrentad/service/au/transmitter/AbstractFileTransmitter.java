package com.darkkaiser.torrentad.service.au.transmitter;

import com.darkkaiser.torrentad.config.Configuration;
import com.darkkaiser.torrentad.util.crypto.AES256Util;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public abstract class AbstractFileTransmitter implements FileTransmitter {
	
	protected final Configuration configuration;

	private AES256Util aes256;

	protected AbstractFileTransmitter(final Configuration configuration) {
		Objects.requireNonNull(configuration, "configuration");

		this.configuration = configuration;
	}

	protected String decode(final String encryption) throws Exception {
		if (this.aes256 == null)
			this.aes256 = new AES256Util();

		try {
			return this.aes256.decode(encryption);
		} catch (final Exception e) {
			log.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", encryption);
			throw e;
		}
	}

}
