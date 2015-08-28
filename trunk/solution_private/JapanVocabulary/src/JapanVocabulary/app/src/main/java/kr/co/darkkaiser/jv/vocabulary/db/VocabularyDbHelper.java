package kr.co.darkkaiser.jv.vocabulary.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kr.co.darkkaiser.jv.common.Constants;

public class VocabularyDbHelper {

	private static final String TAG = "VocabularyDbHelper";

    private static VocabularyDbHelper instance;

    private String vocabularyDbFilePath = null;

    static {
        instance = new VocabularyDbHelper();
    }

	private VocabularyDbHelper() {
	}

	public static VocabularyDbHelper getInstance() {
		return instance;
	}

    public boolean init(Context context) {
		assert context != null;

        String vocabularyDbFilePath = context.getDatabasePath(Constants.VOCABULARY_DB_FILENAME_V3).getAbsolutePath();

		// 'databases' 폴더가 존재하는지 확인하여 존재하지 않는다면 폴더를 생성한다.
		String dbPath = vocabularyDbFilePath.substring(0, vocabularyDbFilePath.length() - Constants.VOCABULARY_DB_FILENAME_V3.length());
		File file = new File(dbPath);
		if (file.exists() == false) {
			if (file.mkdirs() == false) {
                Log.d(TAG, String.format("패키지DB 경로 생성이 실패하였습니다(%s).", dbPath));
                return false;
			}
		}

        this.vocabularyDbFilePath = vocabularyDbFilePath;

		return true;
	}

	public String getVocabularyDbFilePath() {
        return this.vocabularyDbFilePath;
    }

    public String getLatestVocabularyDbVersion() throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            return jsonObject.getString("version");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return "";
    }

    @SuppressWarnings("unused")
    public String getLatestVocabularyDbFileHash() throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            return jsonObject.getString("sha1");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return "";
    }

    @SuppressWarnings("unused")
    public ArrayList<String> getLatestVocabularyDbInfoList() {
        String vocabularyDbVersion = "";
        String vocabularyDbFileHash = "";

        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            vocabularyDbVersion = jsonObject.getString("version");
            vocabularyDbFileHash = jsonObject.getString("sha1");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        ArrayList<String> result = new ArrayList<>();
        result.add(vocabularyDbVersion);
        result.add(vocabularyDbFileHash);

        return result;
    }

    public boolean canUpdateVocabularyDb(SharedPreferences sharedPreferences, String[] result) {
        assert sharedPreferences != null;

        if (result.length != 2) {
            return false;
        }

        String localVocabularyDbVersion = sharedPreferences.getString(Constants.SPKEY_INSTALLED_DB_VERSION, "");

        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            String newVocabularyDbVersion = jsonObject.getString("version");
            String newVocabularyDbFileHash = jsonObject.getString("sha1");

            if (TextUtils.isEmpty(newVocabularyDbVersion) == false && newVocabularyDbVersion.equals(localVocabularyDbVersion) == false) {
                result[0] = newVocabularyDbVersion;
                result[1] = newVocabularyDbFileHash;
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return false;
    }

    private String getStringFromUrl(String urlString) throws UnsupportedEncodingException {
        BufferedReader br = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) sb.append(line);

            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            if (conn != null) {
                conn.disconnect();
            }
        }

        return "";
    }

}
