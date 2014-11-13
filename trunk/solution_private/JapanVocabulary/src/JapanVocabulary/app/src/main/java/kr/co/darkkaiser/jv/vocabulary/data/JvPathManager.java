package kr.co.darkkaiser.jv.vocabulary.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import kr.co.darkkaiser.jv.common.Constants;

// @@@@@ todo
public class JvPathManager {

	private static final String TAG = "JvPathManager";

	private static JvPathManager mInstance = null;

    private String mUserDbFilePath = null;
    private String mVocabularyDbFilePath = null;

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

        String userDbFilePath = context.getDatabasePath(Constants.JV_USER_DB_V3).getAbsolutePath();
        String vocabularyDbFilePath = context.getDatabasePath(Constants.JV_VOCABULARY_DB_V3).getAbsolutePath();

		// 'databases' 폴더가 존재하는지 확인하여 존재하지 않는다면 폴더를 생성한다.
		String packageDbPath = vocabularyDbFilePath.substring(0, vocabularyDbFilePath.length() - Constants.JV_VOCABULARY_DB_V3.length());
		File f = new File(packageDbPath);
		if (f.exists() == false) {
			if (f.mkdirs() == false) {
                Log.d(TAG, String.format("패키지DB 경로 생성이 실패하였습니다(%s).", packageDbPath));
                return false;
			}
		}

        // @@@@@ 이전 DB 마이그레이션

        mUserDbFilePath = userDbFilePath;
        mVocabularyDbFilePath = vocabularyDbFilePath;

		return true;
	}

	public String getVocabularyDbFilePath() {
		assert TextUtils.isEmpty(mVocabularyDbFilePath) == false;
		return mVocabularyDbFilePath;
	}
	
	public String getUserDbFilePath() {
		assert TextUtils.isEmpty(mUserDbFilePath) == false;
		return mUserDbFilePath;
	}

	public boolean readyDbStorage() {
        return !(TextUtils.isEmpty(mVocabularyDbFilePath) == true || TextUtils.isEmpty(mUserDbFilePath) == true);
	}

    public static ArrayList<String> getLatestVocabularyDbInfoList() throws Exception {
        String newVocabularyDbVersion = "", newVocabularyDbFileHash = "";

        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.JV_DB_CHECKSUM_URL));
            newVocabularyDbVersion = jsonObject.getString("version");
            newVocabularyDbFileHash = jsonObject.getString("sha1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> result = new ArrayList<String>();
        result.add(newVocabularyDbVersion);
        result.add(newVocabularyDbFileHash);

        return result;
    }

    public static String getLatestVocabularyDbVersion() throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.JV_DB_CHECKSUM_URL));
            return jsonObject.getString("version");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getLatestVocabularyDbFileHash() throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.JV_DB_CHECKSUM_URL));
            return jsonObject.getString("sha1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getStringFromUrl(String url) throws UnsupportedEncodingException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url), "UTF-8"));

        StringBuilder sb = new StringBuilder();

        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static InputStream getInputStreamFromUrl(String url) {
        InputStream contentStream = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
            contentStream = httpResponse.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentStream;
    }

}
