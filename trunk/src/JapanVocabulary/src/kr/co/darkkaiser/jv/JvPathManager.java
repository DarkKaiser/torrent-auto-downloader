package kr.co.darkkaiser.jv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

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

		String jvVocabularyDbPath = context.getDatabasePath(JvDefines.JV_VOCABULARY_DB).getAbsolutePath();
		String jvUserVocubularyInfoFilePath = context.getDatabasePath(JvDefines.JV_USER_VOCABULARY_INFO_FILE).getAbsolutePath();

		// 'databases' ������ �����ϴ��� Ȯ���Ͽ� �������� �ʴ´ٸ� ������ �����Ѵ�.
		String path = jvVocabularyDbPath.substring(0, jvVocabularyDbPath.length() - JvDefines.JV_VOCABULARY_DB.length());
		File f = new File(path);
		if (f.exists() == false && f.mkdirs() == false) {
			return initSDCard(context);
		}

		// SDCARD ������ ���� �ܾ� DB ������ �����ϴ��� Ȯ���� �� �����Ѵٸ� ������ �� �����Ѵ�.
		String appMainPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
		f = new File(appMainPath);
		if (f.exists() == true) {
			String sdcJvVocabularyDbPath = String.format("%s%s", appMainPath, JvDefines.JV_VOCABULARY_DB);

			// �ܾ� DB ������ Ȯ���Ѵ�.
			f = new File(sdcJvVocabularyDbPath);
			if (f.exists() == true) {
				try {
					copyFile(sdcJvVocabularyDbPath, jvVocabularyDbPath);
				} catch (IOException e) {
					return initSDCard(context);
				}
				
				f.delete();
				
				String sdcJvUserVocubularyInfoFilePath = String.format("%s%s", appMainPath, JvDefines.JV_USER_VOCABULARY_INFO_FILE);

				// ����� ���� DB ������ Ȯ���Ѵ�.
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
					
					// ���ϸ��� ����صд�.
					String sdcJvUserVocubularyInfoBackupFilePath = String.format("%s%s.backup", appMainPath, JvDefines.JV_USER_VOCABULARY_INFO_FILE);
					f.renameTo(new File(sdcJvUserVocubularyInfoBackupFilePath));
				}
			}
		}

		mJvVocabularyDbPath = jvVocabularyDbPath;
		mJvUserVocubularyInfoFilePath = jvUserVocubularyInfoFilePath;

		return true;
	}
	
	private boolean initSDCard(Context context) {
		assert context != null;

		// �����ͺ��̽� ����, ������� �ܾ ���� ������ ������ ������ ��ġ�ϴ� ��θ� ���Ѵ�.
		String appMainPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
		File f = new File(appMainPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		mJvVocabularyDbPath = String.format("%s%s", appMainPath, JvDefines.JV_VOCABULARY_DB);
		mJvUserVocubularyInfoFilePath = String.format("%s%s", appMainPath, JvDefines.JV_USER_VOCABULARY_INFO_FILE);

		try {
			// �ܾ� DB ������ ��ΰ� SDCARD�� ����ϴ� ����ڸ� ī��Ʈ�ϱ� ���� ���������� ȣ���Ѵ�. 
			URL url = new URL("http://darkkaiser.cafe24.com/data/jv_sdcard_check.php");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write("");
			osw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "euc-kr"));
			while (br.readLine() != null) {
			}
			br.close();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		
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
	        int i = 0;
	        byte[] buf = new byte[1024];

	        while ((i = fis.read(buf)) != -1) {
	            fos.write(buf, 0, i);
	        }
	    } catch (IOException e) {
	        throw e;
	    } finally {
	        if (fis != null)
	            fis.close();
	        if (fos != null)
	            fos.close();
	    }
	}
	
}
