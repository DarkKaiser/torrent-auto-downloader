package kr.co.darkkaiser.jv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import kr.co.darkkaiser.jv.common.Constants;

// @@@@@ todo
public class JvPathManager {

	private static final String TAG = "JvPathManager";

	private static JvPathManager mInstance = null;
	
	private String mJvVocabularyDbPath = null;
	private String mJvUserVocubularyInfoFilePath = null;

	static {
		mInstance = new JvPathManager();
	}

	public JvPathManager() {

	}

	public static JvPathManager getInstance() {
		return mInstance;
	}

	public boolean init(Context context) {
		assert context != null;
		
		String jvVocabularyDbPath = context.getDatabasePath(Constants.JV_VOCABULARY_DB).getAbsolutePath();
		String jvUserVocubularyInfoFilePath = context.getDatabasePath(Constants.JV_USER_VOCABULARY_INFO_FILE).getAbsolutePath();
		
		// 'databases' 폴더가 존재하는지 확인하여 존재하지 않는다면 폴더를 생성한다.
		String path = jvVocabularyDbPath.substring(0, jvVocabularyDbPath.length() - Constants.JV_VOCABULARY_DB.length());
		File f = new File(path);
		if (f.exists() == false) {
			if (f.mkdirs() == false) {
				return false;
			}
		}

		// SDCARD 영역에 기존 단어 DB 파일이 존재하는지 확인한 후 존재한다면 복사한 후 삭제한다.
		String appMainPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.JV_MAIN_FOLDER_NAME);
		f = new File(appMainPath);
		if (f.exists() == true) {
			String sdcJvVocabularyDbPath = String.format("%s%s", appMainPath, Constants.JV_VOCABULARY_DB);

			// 단어 DB 파일을 확인한다.
			f = new File(sdcJvVocabularyDbPath);
			if (f.exists() == true) {
				try {
					copyFile(sdcJvVocabularyDbPath, jvVocabularyDbPath);
				} catch (IOException e) {
				}
				
				f.delete();
				
				String sdcJvUserVocubularyInfoFilePath = String.format("%s%s", appMainPath, Constants.JV_USER_VOCABULARY_INFO_FILE);

				// 사용자 정보 DB 파일을 확인한다.
				f = new File(sdcJvUserVocubularyInfoFilePath);
				if (f.exists() == true) {
					File f2 = new File(jvUserVocubularyInfoFilePath);
					if (f2.exists() == false) {
						try {
							copyFile(sdcJvUserVocubularyInfoFilePath, jvUserVocubularyInfoFilePath);
						} catch (IOException e) {
							Log.d(TAG, e.getMessage());
						}
					}
					
					// 파일명을 백업해둔다.
					String sdcJvUserVocubularyInfoBackupFilePath = String.format("%s%s.backup", appMainPath, Constants.JV_USER_VOCABULARY_INFO_FILE);
					f.renameTo(new File(sdcJvUserVocubularyInfoBackupFilePath));
				}
			}
		}

		mJvVocabularyDbPath = jvVocabularyDbPath;
		mJvUserVocubularyInfoFilePath = jvUserVocubularyInfoFilePath;

		return true;
	}

	public String getVocabularyDbPath() {
		assert TextUtils.isEmpty(mJvVocabularyDbPath) == false;
		return mJvVocabularyDbPath;
	}
	
	public String getUserVocabularyInfoFilePath() {
		assert TextUtils.isEmpty(mJvUserVocubularyInfoFilePath) == false;
		return mJvUserVocubularyInfoFilePath;
	}

	public boolean isReadyIoDevice() {
		if (TextUtils.isEmpty(mJvVocabularyDbPath) == true || TextUtils.isEmpty(mJvUserVocubularyInfoFilePath) == true) {
			return false;
		}

		return true;
		// return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private void copyFile(String srcFilePath, String tgtFilePath) throws IOException {
		assert TextUtils.isEmpty(srcFilePath) == false;
		assert TextUtils.isEmpty(tgtFilePath) == false;

	    FileInputStream fis = new FileInputStream(srcFilePath);
	    FileOutputStream fos = new FileOutputStream(tgtFilePath);

	    try {
	        int i;
	        byte[] buf = new byte[1024];

	        while ((i = fis.read(buf)) != -1) {
	            fos.write(buf, 0, i);
	        }
	    } catch (IOException e) {
	        throw e;
	    } finally {
            fis.close();
            fos.close();
	    }
	}
	
}
