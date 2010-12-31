package kr.co.darkkaiser.jv.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHash {
	public static byte[] getHash(File file) throws IOException, NoSuchAlgorithmException {
		assert file != null;
		assert file.exists() == true;

	    BufferedInputStream bis = null;

	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA1");
	        bis = new BufferedInputStream(new FileInputStream(file));
	        
	        int readBytes = -1;
	        byte[] buffer = new byte[1024];
	        while ((readBytes = bis.read(buffer)) != -1) {
	            md.update(buffer, 0, readBytes);
	        }

	        return md.digest();
	    } finally {
	        if (bis != null) {
	        	try {
	        		bis.close();
	        	} catch (IOException e) {
	        	}
	        }
	    }
	}
}
