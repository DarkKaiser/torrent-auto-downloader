package kr.co.darkkaiser.jv.vocabulary.db;

import android.content.Context;
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

public class VocabularyDbManager {

	private static final String TAG = "VocabularyDbManager";

    private static VocabularyDbManager mInstance;

    private String mVocabularyDbFilePath = null;

    static {
        mInstance = new VocabularyDbManager();
    }

	private VocabularyDbManager() {

	}

	public static VocabularyDbManager getInstance() {
		return mInstance;
	}

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean init(Context context) {
		assert context != null;

        String v3UserDbFilePath = context.getDatabasePath(Constants.USER_DB_FILENAME_V3).getAbsolutePath();
        String v3VocabularyDbFilePath = context.getDatabasePath(Constants.VOCABULARY_DB_FILENAME_V3).getAbsolutePath();

		// 'databases' 폴더가 존재하는지 확인하여 존재하지 않는다면 폴더를 생성한다.
		String dbPath = v3VocabularyDbFilePath.substring(0, v3VocabularyDbFilePath.length() - Constants.VOCABULARY_DB_FILENAME_V3.length());
		File file1 = new File(dbPath);
		if (file1.exists() == false) {
			if (file1.mkdirs() == false) {
                Log.d(TAG, String.format("패키지DB 경로 생성이 실패하였습니다(%s).", dbPath));
                return false;
			}
		}

        // @@@@@ v2 파일이 db가 아니므로 수정이 필요함, VocabularyManager로 함수를 이동할지 고민
        // 사용자의 암기정보를 저장한 DB 파일을 마이그레이션 한다.(버전 2 -> 3)
        String v2UserDbFilePath = context.getDatabasePath(Constants.USER_DB_FILENAME_V2).getAbsolutePath();
        file1 = new File(v2UserDbFilePath);
        if (file1.exists() == true) {
            File file2 = new File(v3UserDbFilePath);
            if (file2.exists() == false) file1.renameTo(new File(v3UserDbFilePath));
            else file1.delete();
        }

        // 단어DB 파일 정보를 저장한다.
        mVocabularyDbFilePath = v3VocabularyDbFilePath;

		return true;
	}

	public String getVocabularyDbFilePath() { return mVocabularyDbFilePath; }

    public String getLatestVocabularyDbVersion() throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            return jsonObject.getString("version");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getLatestVocabularyDbFileHash() throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            return jsonObject.getString("sha1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public ArrayList<String> getLatestVocabularyDbInfoList() throws Exception {
        String vocabularyDbVersion = "";
        String vocabularyDbFileHash = "";

        try {
            JSONObject jsonObject = new JSONObject(getStringFromUrl(Constants.VOCABULARY_DB_CHECKSUM_URL));
            vocabularyDbVersion = jsonObject.getString("version");
            vocabularyDbFileHash = jsonObject.getString("sha1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> result = new ArrayList<String>();
        result.add(vocabularyDbVersion);
        result.add(vocabularyDbFileHash);

        return result;
    }

    private String getStringFromUrl(String url) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url), "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private InputStream getInputStreamFromUrl(String url) {
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
