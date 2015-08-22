package kr.co.darkkaiser.jv.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// @@@@@
public class FileHash {

	public static byte[] getHash(File file) throws IOException, NoSuchAlgorithmException {
		assert file != null;
		assert file.exists();

	    BufferedInputStream bis = null;

	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA1");
	        bis = new BufferedInputStream(new FileInputStream(file));
	        
	        int readBytes;
	        byte[] buffer = new byte[1024];
	        while ((readBytes = bis.read(buffer)) != -1)
	            md.update(buffer, 0, readBytes);

	        return md.digest();
	    } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException ignored) {
            }
	    }
	}

}
