package kr.co.darkkaiser.jv;

import java.io.File;

import android.os.Environment;
import android.text.TextUtils;

public class JvPathManager {

	private static JvPathManager mInstance = null;
	
	private String mJvVocabularyDbPath = null;
	private String mJvUserVocubularyInfoFilePath = null;

	static {
		mInstance = new JvPathManager();
	}

	public JvPathManager() {
		// 데이터베이스 파일, 사용자의 단어에 대한 정보를 저장한 파일이 위치하는 경로를 구한다.
		String appMainPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
		File f = new File(appMainPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		mJvVocabularyDbPath = String.format("%s%s", appMainPath, JvDefines.JV_VOCABULARY_DB);
		mJvUserVocubularyInfoFilePath = String.format("%s%s", appMainPath, JvDefines.JV_USER_VOCABULARY_INFO_FILE);
	}

	public static JvPathManager getInstance() {
		return mInstance;
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
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

}
