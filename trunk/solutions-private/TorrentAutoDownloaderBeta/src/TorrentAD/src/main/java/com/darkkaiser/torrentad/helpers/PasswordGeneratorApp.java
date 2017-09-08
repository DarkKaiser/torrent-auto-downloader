package com.darkkaiser.torrentad.helpers;

import com.darkkaiser.torrentad.util.crypto.AES256Util;

public class PasswordGeneratorApp {

	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("암복호화 하시려는 비밀번호를 입력하여 주세요.");
			return;
		}

        String plainText = args[0];

        AES256Util aes256 = new AES256Util();

        String encText = aes256.encode(plainText);
        String decText = aes256.decode(encText);

        System.out.println("암호화할 문자 : " + plainText);
        System.out.println("암호화된 문자 : " + encText);
        System.out.println("복호화된 문자 : " + decText);
	}

}
