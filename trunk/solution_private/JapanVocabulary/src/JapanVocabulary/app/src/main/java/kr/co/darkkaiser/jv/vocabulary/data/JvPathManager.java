package kr.co.darkkaiser.jv.vocabulary.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import kr.co.darkkaiser.jv.common.Constants;

// @@@@@ todo
public class JvPathManager {

	private static final String TAG = "JvPathManager";

	private static JvPathManager mInstance = null;

	private String mVocabularyDbPath = null;
	private String mUserDbPath = null;

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

        String userDbPath = context.getDatabasePath(Constants.JV_USER_DB).getAbsolutePath();
        String vocabularyDbPath = context.getDatabasePath(Constants.JV_VOCABULARY_DB).getAbsolutePath();

		// 'databases' 폴더가 존재하는지 확인하여 존재하지 않는다면 폴더를 생성한다.
		String path = vocabularyDbPath.substring(0, vocabularyDbPath.length() - Constants.JV_VOCABULARY_DB.length());
		File f = new File(path);
		if (f.exists() == false) {
			if (f.mkdirs() == false) {
                Log.d(TAG, "@@@@@");
                return false;
			}
		}

        // @@@@@ DB 마이그레이션

        mUserDbPath = userDbPath;
        mVocabularyDbPath = vocabularyDbPath;

		return true;
	}

	public String getVocabularyDbPath() {
		assert TextUtils.isEmpty(mVocabularyDbPath) == false;
		return mVocabularyDbPath;
	}
	
	public String getUserVocabularyInfoFilePath() {
		assert TextUtils.isEmpty(mUserDbPath) == false;
		return mUserDbPath;
	}

	public boolean isReadyIoDevice() {
        return !(TextUtils.isEmpty(mVocabularyDbPath) == true || TextUtils.isEmpty(mUserDbPath) == true);
	}

}
