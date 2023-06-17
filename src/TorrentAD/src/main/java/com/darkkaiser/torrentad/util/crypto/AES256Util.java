package com.darkkaiser.torrentad.util.crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AES256Util {

	/** 암복호화 키(최소 16자 이상) */
	private static final String DEFAULT_CRYPTOGRAPH_KEY = "chlrhrkehlwk9876@*!";

	private final String iv;
	private final Key keySpec;

	public AES256Util() throws UnsupportedEncodingException {
		this(DEFAULT_CRYPTOGRAPH_KEY);
	}

	private AES256Util(final String key) throws UnsupportedEncodingException {
		this.iv = key.substring(0, 16);

		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes(StandardCharsets.UTF_8);
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;

		System.arraycopy(b, 0, keyBytes, 0, len);

		this.keySpec = new SecretKeySpec(keyBytes, "AES");
	}

	public String encode(final String str) throws UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

		byte[] encrypted = c.doFinal(str.getBytes(StandardCharsets.UTF_8));

		return new String(Base64.encodeBase64(encrypted));
	}

	public String decode(final String str) throws UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));

		byte[] byteStr = Base64.decodeBase64(str.getBytes());

		return new String(c.doFinal(byteStr), StandardCharsets.UTF_8);
	}

}
