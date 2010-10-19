package kr.co.darkkaiser.jv;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class JvManager {

	private static final String TAG = "JvManager";

	private static JvManager mInstance = null;
	
	/*
	 * SDCARD�� �����Ǵ� �� ������
	 */
	public static final String JV_MAIN_FOLDER_NAME = "JapanVocabulary";
	
	/*
	 * �Ϻ��� �ܾ� DB ���ϸ�
	 */
    private static final String JV_VOCABULARY_DB = "jv2.db";

    /*
     * ����� DB ���ϸ�
     */
    private static final String JV_USER_DB = "jv2_user.db";

    // �Ϻ��� �ܾ� DB, ����� DB ��ü ��� 
	private String mJvUserDbPath = null;
	private String mJvVocabularyDbPath = null;
	
	// �Ϻ��� �ܾ� DB, ����� DB ���� SQLite ��ü
	private SQLiteDatabase mJvUserSqLite = null;
	private SQLiteDatabase mJvVocabularySqLite = null;

	/*
	 * ��ü �Ϻ��� �ܾ� ����Ʈ ���̺�
	 */
	private Hashtable<Long, JapanVocabulary> mJvTable = null;

	static {
		mInstance = new JvManager();
	}

	private JvManager() {
		mJvTable = new Hashtable<Long, JapanVocabulary>();

		// �����ͺ��̽� ������ ��ġ�ϴ� ��θ� ���Ѵ�.
		String dbPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JV_MAIN_FOLDER_NAME);
		File f = new File(dbPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		mJvUserDbPath = String.format("%s%s", dbPath, JV_USER_DB);;
		mJvVocabularyDbPath = String.format("%s%s", dbPath, JV_VOCABULARY_DB);
	}

	public static JvManager getInstance() {
		return mInstance;
	}

	public synchronized boolean initDataFromDB() {
		// ������ ��ϵ� ��� �ܾ �����Ѵ�.
		if (mJvTable.isEmpty() == false)
			mJvTable.clear();

		try {
			if (mJvUserSqLite != null)
			{
				mJvUserSqLite.close();
				mJvUserSqLite = null;
			}

			if (mJvVocabularySqLite != null)
			{
				mJvVocabularySqLite.close();
				mJvVocabularySqLite = null;
			}

			// �Ϻ��� �ܾ �о���δ�.
			assert TextUtils.isEmpty(mJvVocabularyDbPath) == false;
			mJvVocabularySqLite = SQLiteDatabase.openDatabase(mJvVocabularyDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mJvVocabularySqLite == null)
				return false;

			Cursor cursor = mJvVocabularySqLite.rawQuery("SELECT IDX, " +
														 "       VOCABULARY, " +
														 "       VOCABULARY_GANA, " +
														 "       VOCABULARY_TRANSLATION, " +
														 "       REGISTRATION_DATE, " +
														 "       PARTS_OF_SPEECH " +
														 "  FROM TBL_VOCABULARY", null);

			if (cursor.moveToFirst() == true) {
				do
				{
					long idx = cursor.getLong(0/* IDX */);
					
		    		mJvTable.put(idx, new JapanVocabulary(idx,
		    											  cursor.getLong(4/* REGISTRATION_DATE */),
		    											  cursor.getString(1/* VOCABULARY */),
		    											  cursor.getString(2/* VOCABULARY_GANA */),
		    											  cursor.getString(3/* VOCABULARY_TRANSLATION */),
		    											  cursor.getLong(5/* PARTS_OF_SPEECH */)));
				} while (cursor.moveToNext());
			}

			cursor.close();

			// @@@@@ db�� ���� ���� db�� �ڵ����� �����ǵ��� ����� ��.
			// ����� DB���� �ܾ� �ϱ⿡ ���� ������ �о���δ�.
			assert TextUtils.isEmpty(mJvUserDbPath) == false;
			mJvUserSqLite = SQLiteDatabase.openDatabase(mJvUserDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mJvVocabularySqLite != null)
			{
				// @@@@@ ��񿡼� �о�鿩�� ������Ʈ
			}
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}

        return true;
	}

	// @@@@@
	public synchronized void searchJapanVocabulary(String searchWord, long searchDateFirst, long searchDateLast, ArrayList<JapanVocabulary> jvList) {
		if (TextUtils.isEmpty(searchWord) == true) {
			if (searchDateFirst == -1 || searchDateLast == -1) {
				for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
					jvList.add(e.nextElement());
				}
			} else {
				assert searchDateFirst < searchDateLast;
				for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
					JapanVocabulary jv = e.nextElement();
					if (jv.getRegistrationDate() >= searchDateFirst && jv.getRegistrationDate() <= searchDateLast)
						jvList.add(jv);
				}
			}
		} else {
			if (searchDateFirst == -1 || searchDateLast == -1) {
				for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
					JapanVocabulary jv = e.nextElement();
					if (jv.getVocabularyTranslation().contains(searchWord) == true)
						jvList.add(jv);
				}
			} else {
				assert searchDateFirst < searchDateLast;
				for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
					JapanVocabulary jv = e.nextElement();
					if (jv.getRegistrationDate() >= searchDateFirst && jv.getRegistrationDate() <= searchDateLast &&
							jv.getVocabularyTranslation().contains(searchWord) == true) {
						jvList.add(jv);
					}
				}
			}
		}
	}

	// @@@@@
	public synchronized JapanVocabulary getJapanVocabulary(long idx) {
		return mJvTable.get(idx);
	}

	// @@@@@
	public synchronized void getMemorizeTargetVocabulary(ArrayList<JapanVocabulary> jvList) {
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true && jv.isMemorizeCompleted() == false)
				jvList.add(jv);
		}
	}

	// @@@@@
	public synchronized void rememorizeAllMemorizeTarget() {
		if (mJvVocabularySqLite != null) {
			mJvVocabularySqLite.execSQL("update tbl_vocabulary set memorize_completed=0 where memorize_target=1");

			// DB���� �����͸� �ٽ� �о���δ�.
			initDataFromDB();
		} else {
			assert false;
		}
	}

	// @@@@@
	public synchronized void updateMemorizeCompleted(long idx, boolean flag) {
		if (mJvVocabularySqLite != null) {
			mJvVocabularySqLite.execSQL(String.format("update tbl_vocabulary set memorize_completed=%d where idx=%d", flag ? 1 : 0, idx));
		} else {
			assert false;
		}
	}

	// @@@@@
	public synchronized void updateMemorizeTarget(long idx, boolean flag) {
		if (mJvVocabularySqLite != null) {
			mJvVocabularySqLite.execSQL(String.format("update tbl_vocabulary set memorize_target=%d where idx=%d", flag ? 1 : 0, idx));
		} else {
			assert false;
		}
	}
	
	// @@@@@
	public void resetMemorizeInfo(int menuItemId, ArrayList<Long> idxList) {
		if (mJvVocabularySqLite != null) {
			String contentValue = "";

			switch (menuItemId) {
			case R.id.jvlm_all_rememorize:				// �˻��� ��ü �ܾ� ��ϱ�
				contentValue = "memorize_completed=0";
				break;
			case R.id.jvlm_all_memorize_completed:		// �˻��� ��ü �ܾ� �ϱ� �Ϸ�
				contentValue = "memorize_completed=1";
				break;
			case R.id.jvlm_all_memorize_target:			// �˻��� ��ü �ܾ� �ϱ� ��� �����
				contentValue = "memorize_target=1";
				break;
			case R.id.jvlm_all_memorize_target_cancel:	// �˻��� ��ü �ܾ� �ϱ� ��� ����
				contentValue = "memorize_target=0";
				break;
			default:
				assert false;
				break;
			}
			
			String idx_list = "";
			for (int index = 0; index < idxList.size(); ++index) {
				if (TextUtils.isEmpty(idx_list) == false) {
					idx_list += ", ";
				}
				
				idx_list += idxList.get(index).toString();
			}
			
			try {
				mJvVocabularySqLite.execSQL(String.format("update tbl_vocabulary set %s where idx in(%s)", contentValue, idx_list));				
			} catch (SQLiteException e) {
			}
			
			// DB���� �����͸� �ٽ� �о���δ�.
			initDataFromDB();
		} else {
			assert false;
		}
	}

	public String getJapanVocabularyDetailDescription(String vocabulary) {
		assert TextUtils.isEmpty(vocabulary) == false;

		String result = "";
		if (mJvVocabularySqLite != null) {
			for (int index = 0; index < vocabulary.length(); ++index) {
				try {
					Cursor cursor = mJvVocabularySqLite.rawQuery(String.format("SELECT CHARACTER, " +
																			   "       SOUND_READ, " +
																			   "       MEAN_READ, " +
																			   "       JLPT_CLASS, " +
																			   "       TRANSLATION " +
																			   "  FROM TBL_HANJA " + 
																			   " WHERE CHARACTER == '%s'", vocabulary.charAt(index)), null);
					if (cursor.moveToFirst() == true) {
						assert cursor.getCount() == 1;

						if (index > 0)
							result += "\n\n";
						
						// @@@@@ JLPT ���� ���
						result += String.format("%s\n%s\n����: %s\n�Ƶ�: %s", cursor.getString(0/* CHARACTER */), cursor.getString(4/* TRANSLATION */), cursor.getString(1/* SOUND_READ */), cursor.getString(2/* MEAN_READ */));
					}
					
					cursor.close();
				} catch (SQLiteException e) {
				}
			}			
		}
		
		return result;
	}

}
