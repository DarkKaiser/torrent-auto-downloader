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

public class JapanVocabularyManager {

	private static JapanVocabularyManager mInstance = null;

	private String mJvDbPath = null;
	public SQLiteDatabase mSqlite = null;

	private Hashtable<Long, JapanVocabulary> mJvTable = null;

	static {
		mInstance = new JapanVocabularyManager();
	}
	
	private JapanVocabularyManager() {
		mJvTable = new Hashtable<Long, JapanVocabulary>();
	
		mJvDbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JapanVocabulary/";
		File f = new File(mJvDbPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		mJvDbPath += "jv.db";
	}

	public static JapanVocabularyManager getInstance() {
		return mInstance;
	}

	public synchronized boolean initDataFromDB() {
		// 등록된 모든 단어를 제거한다.
		if (mJvTable.isEmpty() == false)
			mJvTable.clear();

		try {
			if (mSqlite != null)
				mSqlite.close();

			mSqlite = SQLiteDatabase.openDatabase(mJvDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mSqlite == null)
				return false;

			Cursor cursor = mSqlite.rawQuery("select * from tbl_vocabulary", null);
			if (cursor.moveToFirst() == true) {
				do
				{
					long index = cursor.getInt(0/* idx */);
					boolean bIsMemorizeTarget = cursor.getInt(5) == 1 ? true : false;
					boolean bIsMemorizeCompleted = cursor.getInt(4) == 1 ? true : false;
					
		    		mJvTable.put(index, new JapanVocabulary(index,
		    				cursor.getLong(6/* 등록일 */),
		    				cursor.getString(1/* 단어 */),
		    				cursor.getString(2/* 히라가나/가타가나 */),
		    				cursor.getString(3/* 뜻 */),
		    				bIsMemorizeTarget,
		    				bIsMemorizeCompleted));
				} while (cursor.moveToNext());
			}

			cursor.close();
		} catch (SQLiteException e) {
			Log.d("JapanVocabulary", e.getMessage());
			return false;
		}

        return true;
	}

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

	public synchronized JapanVocabulary getJapanVocabulary(long idx) {
		return mJvTable.get(idx);
	}

	public synchronized void getMemorizeTargetVocabulary(ArrayList<JapanVocabulary> jvList) {
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true && jv.isMemorizeCompleted() == false)
				jvList.add(jv);
		}
	}

	public synchronized void rememorizeAllMemorizeTarget() {
		if (mSqlite != null) {
			mSqlite.execSQL("update tbl_vocabulary set memorize_completed=0 where memorize_target=1");

			// DB에서 데이터를 다시 읽어들인다.
			initDataFromDB();
		} else {
			assert false;
		}
	}

	public synchronized void updateMemorizeCompleted(long idx, boolean flag) {
		if (mSqlite != null) {
			mSqlite.execSQL(String.format("update tbl_vocabulary set memorize_completed=%d where idx=%d", flag ? 1 : 0, idx));
		} else {
			assert false;
		}
	}

	public synchronized void updateMemorizeTarget(long idx, boolean flag) {
		if (mSqlite != null) {
			mSqlite.execSQL(String.format("update tbl_vocabulary set memorize_target=%d where idx=%d", flag ? 1 : 0, idx));
		} else {
			assert false;
		}
	}
	
	public void resetMemorizeInfo(int menuItemId, ArrayList<Long> idxList) {
		if (mSqlite != null) {
			String contentValue = "";

			switch (menuItemId) {
			case R.id.jvlm_all_rememorize:				// 검색된 전체 단어 재암기
				contentValue = "memorize_completed=0";
				break;
			case R.id.jvlm_all_memorize_completed:		// 검색된 전체 단어 암기 완료
				contentValue = "memorize_completed=1";
				break;
			case R.id.jvlm_all_memorize_target:			// 검색된 전체 단어 암기 대상 만들기
				contentValue = "memorize_target=1";
				break;
			case R.id.jvlm_all_memorize_target_cancel:	// 검색된 전체 단어 암기 대상 해제
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
				mSqlite.execSQL(String.format("update tbl_vocabulary set %s where idx in(%s)", contentValue, idx_list));				
			} catch (SQLiteException e) {
			}
			
			// DB에서 데이터를 다시 읽어들인다.
			initDataFromDB();
		} else {
			assert false;
		}
	}

	public String getJapanVocabularyDetailInfo(String vocabulary) {
		String result = "";

		if (mSqlite != null) {
			for (int index = 0; index < vocabulary.length(); ++index) {
				try {
					Cursor cursor = mSqlite.rawQuery(String.format("select * from tbl_hanja where Word='%s'", vocabulary.charAt(index)), null);
					if (cursor.moveToFirst() == true) {
						do
						{
							result += String.format("%s\n%s\n음독: %s\n훈독: %s\n\n", cursor.getString(1), cursor.getString(4), cursor.getString(2), cursor.getString(3));
						} while (cursor.moveToNext());
					}
					
					cursor.close();					
				} catch (SQLiteException e) {
				}
			}			
		}
		
		return result;
	}

}
