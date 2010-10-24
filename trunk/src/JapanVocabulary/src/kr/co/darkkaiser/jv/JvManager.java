package kr.co.darkkaiser.jv;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import kr.co.darkkaiser.jv.R.menu;
import kr.co.darkkaiser.jv.list.JvListSearchCondition;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class JvManager {

	private static final String TAG = "JvManager";

	private static JvManager mInstance = null;

    // 일본어 단어 DB, 사용자 DB 전체 경로 
	private String mJvUserDbPath = null;
	private String mJvVocabularyDbPath = null;
	
	// 일본어 단어 DB, 사용자 DB 접근 SQLite 객체
	private SQLiteDatabase mJvUserSqLite = null;
	private SQLiteDatabase mJvVocabularySqLite = null;

	/*
	 * 전체 일본어 단어 리스트 테이블
	 */
	private Hashtable<Long, JapanVocabulary> mJvTable = null;

	static {
		mInstance = new JvManager();
	}

	private JvManager() {
		mJvTable = new Hashtable<Long, JapanVocabulary>();

		// 데이터베이스 파일이 위치하는 경로를 구한다.
		String dbPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
		File f = new File(dbPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		mJvUserDbPath = String.format("%s%s", dbPath, JvDefines.JV_USER_DB);;
		mJvVocabularyDbPath = String.format("%s%s", dbPath, JvDefines.JV_VOCABULARY_DB);
	}

	public static JvManager getInstance() {
		return mInstance;
	}

	public synchronized boolean initDataFromDB() {
		// 이전에 등록된 모든 단어를 제거한다.
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

			// 일본어 단어를 읽어들인다.
			assert TextUtils.isEmpty(mJvVocabularyDbPath) == false;
			mJvVocabularySqLite = SQLiteDatabase.openDatabase(mJvVocabularyDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mJvVocabularySqLite == null)
				return false;
			
			StringBuilder sb = new StringBuilder();
			sb.append("         SELECT V.IDX, ")
			  .append("                V.VOCABULARY, ")
			  .append("                V.VOCABULARY_GANA, ")
			  .append("                V.VOCABULARY_TRANSLATION, ")
			  .append("                V.REGISTRATION_DATE, ")
			  .append("                TBL_PARTS_OF_SPEECH.NAME ")
			  .append("           FROM TBL_VOCABULARY AS V ")
			  .append("LEFT OUTER JOIN TBL_PARTS_OF_SPEECH")
			  .append("             ON V.PARTS_OF_SPEECH = TBL_PARTS_OF_SPEECH.IDX");

			Cursor cursor = mJvVocabularySqLite.rawQuery(sb.toString(), null);

			if (cursor.moveToFirst() == true) {
				do
				{
					long idx = cursor.getLong(0/* IDX */);
					
		    		mJvTable.put(idx, new JapanVocabulary(idx,
		    											  cursor.getLong(4/* REGISTRATION_DATE */),
		    											  cursor.getString(1/* VOCABULARY */),
		    											  cursor.getString(2/* VOCABULARY_GANA */),
		    											  cursor.getString(3/* VOCABULARY_TRANSLATION */),
		    											  cursor.getString(5/* PARTS_OF_SPEECH */)));
				} while (cursor.moveToNext());
			}

			cursor.close();

			// 사용자 DB에서 단어 암기에 대한 정보를 읽어들인다.
			assert TextUtils.isEmpty(mJvUserDbPath) == false;
			mJvUserSqLite = SQLiteDatabase.openDatabase(mJvUserDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mJvUserSqLite != null) {
				// @@@@@ db가 처음 생성된 경우에는 테이블을 자동으로 생성해준다.

				cursor = mJvUserSqLite.rawQuery("SELECT V_IDX, " +
												"       MEMORIZE_TARGET, " +
												"       MEMORIZE_COMPLETED, " +
												"       MEMORIZE_COMPLETED_COUNT " +
												"  FROM TBL_USER_VOCABULARY", null);

				if (cursor.moveToFirst() == true) {
					do
					{
						long idx = cursor.getLong(0/* V_IDX */);
						
						JapanVocabulary jv = mJvTable.get(idx);
						if (jv != null) {
							jv.setFirstOnceMemorizeCompletedCount(cursor.getLong(3/* MEMORIZE_COMPLETED_COUNT */));
							jv.setMemorizeTarget(cursor.getLong(1/* MEMORIZE_TARGET */) == 1 ? true : false, false);
							jv.setMemorizeCompleted(cursor.getLong(2/* MEMORIZE_COMPLETED */) == 1 ? true : false, false, false);
						} else {
							assert false;
						}
					} while (cursor.moveToNext());
				}

				cursor.close();
			}
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}

        return true;
	}

	public void searchVocabulary(JvListSearchCondition mJvListSearchCondition, ArrayList<JapanVocabulary> jvList) {
		// @@@@@
		//searchJapanVocabulary("", -1, -1, jvList);
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

	public synchronized JapanVocabulary getJapanVocabulary(long idx) {
		return mJvTable.get(idx);
	}

	public synchronized int getMemorizeTargetJvList(ArrayList<JapanVocabulary> jvList) {
		int mMemorizeTargetJvCount = 0;
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true) {
				++mMemorizeTargetJvCount;
				if (jv.isMemorizeCompleted() == false)
					jvList.add(jv);
			}
		}
		
		return mMemorizeTargetJvCount;
	}

	public synchronized void rememorizeAllMemorizeTarget() {
		if (mJvUserSqLite != null) {
			try {
				StringBuilder sbSQL = new StringBuilder();
				sbSQL.append("UPDATE TBL_USER_VOCABULARY ")
					 .append("   SET MEMORIZE_COMPLETED=0 ")
					 .append(" WHERE MEMORIZE_TARGET=1");

				mJvUserSqLite.execSQL(sbSQL.toString());

				for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
					JapanVocabulary jv = e.nextElement();
					if (jv.isMemorizeTarget() == true)
						jv.setMemorizeCompleted(false, false, false);
				}
			} catch (SQLiteException e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			assert false;
		}
	}

	public synchronized void updateMemorizeTarget(long idx, boolean flag) {
		if (mJvUserSqLite != null) {
			try {
				StringBuilder sbSQL = new StringBuilder();
				sbSQL.append("INSERT OR REPLACE INTO TBL_USER_VOCABULARY ")
					 .append("                       (V_IDX, MEMORIZE_TARGET) ")
					 .append("                VALUES (").append(idx).append(", ").append(flag ? 1 : 0).append(")");

				mJvUserSqLite.execSQL(sbSQL.toString());			
			} catch (SQLiteException e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			assert false;
		}
	}

	public synchronized void updateMemorizeCompleted(long idx, boolean flag, long memorizeCompletedCount) {
		if (mJvUserSqLite != null) {
			try {
				StringBuilder sbSQL = new StringBuilder();
				sbSQL.append("INSERT OR REPLACE INTO TBL_USER_VOCABULARY ")
					 .append("                       (V_IDX, MEMORIZE_COMPLETED, MEMORIZE_COMPLETED_COUNT) ")
					 .append("                VALUES (").append(idx).append(", ").append(flag ? 1 : 0).append(", ").append(memorizeCompletedCount).append(")");

				mJvUserSqLite.execSQL(sbSQL.toString());				
			} catch (SQLiteException e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			assert false;
		}
	}

	public boolean updateMemorizeField(int menuItemId, ArrayList<Long> idxList) {
		if (mJvUserSqLite == null)
			return false;
		
		String columnNameList, columnValueList;

		switch (menuItemId) {
			case R.id.jvlm_all_rememorize:				// 검색된 전체 단어 재암기
				columnNameList = "MEMORIZE_COMPLETED, MEMORIZE_TARGET";
				columnValueList = "0, 1";
				break;
			case R.id.jvlm_all_memorize_completed:		// 검색된 전체 단어 암기 완료 
				columnNameList = "MEMORIZE_COMPLETED, MEMORIZE_COMPLETED_COUNT";
				columnValueList = "1";
				break;
			case R.id.jvlm_all_memorize_target:			// 검색된 전체 단어 암기 대상 만들기
				columnNameList = "MEMORIZE_TARGET";
				columnValueList = "1";
				break;
			case R.id.jvlm_all_memorize_target_cancel:	// 검색된 전체 단어 암기 대상 해제
				columnNameList = "MEMORIZE_TARGET";
				columnValueList = "0";
				break;
			default:
				assert false;
				return false;
		}

		mJvUserSqLite.beginTransaction();

		try {
			StringBuilder sbSQL = new StringBuilder();
			for (int index = 0; index < idxList.size(); ++index) {
				sbSQL.append("INSERT OR REPLACE INTO TBL_USER_VOCABULARY ")
				     .append("                       (V_IDX, ").append(columnNameList).append(") ");

				if (menuItemId == R.id.jvlm_all_memorize_completed) {
					sbSQL.append(" VALUES (").append(idxList.get(index)).append(", ").append(columnValueList).append(", ");
					sbSQL.append("         COALESCE((SELECT MEMORIZE_COMPLETED_COUNT+1 ")
						 .append("                     FROM TBL_USER_VOCABULARY ")
						 .append("                    WHERE V_IDX=").append(idxList.get(index)).append("), 1))");
				} else {
					sbSQL.append(" VALUES (").append(idxList.get(index)).append(", ").append(columnValueList).append(")");
				}

				mJvUserSqLite.execSQL(sbSQL.toString());

				sbSQL.delete(0, sbSQL.length());
			}

			mJvUserSqLite.setTransactionSuccessful();
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			mJvUserSqLite.endTransaction();				
		}

		if (menuItemId == R.id.jvlm_all_rememorize) {							// 검색된 전체 단어 재암기
			JapanVocabulary jv = null;
			for (int index = 0; index < idxList.size(); ++index) {
				jv = mJvTable.get(idxList.get(index));

				jv.setMemorizeTarget(true, false);
				jv.setMemorizeCompleted(false, false, false);
			}
		} else if (menuItemId == R.id.jvlm_all_memorize_completed) {			// 검색된 전체 단어 암기 완료
			for (int index = 0; index < idxList.size(); ++index)
				mJvTable.get(idxList.get(index)).setMemorizeCompleted(true, true, false);
		} else if (menuItemId == R.id.jvlm_all_memorize_target) {				// 검색된 전체 단어 암기 대상 만들기
			for (int index = 0; index < idxList.size(); ++index)
				mJvTable.get(idxList.get(index)).setMemorizeTarget(true, false);
		} else if (menuItemId == R.id.jvlm_all_memorize_target_cancel) {		// 검색된 전체 단어 암기 대상 해제
			for (int index = 0; index < idxList.size(); ++index)
				mJvTable.get(idxList.get(index)).setMemorizeTarget(false, false);
		}

		return true;
	}

	public String getJapanVocabularyDetailDescription(String vocabulary) {
		StringBuilder sbResult = new StringBuilder();
		if (mJvVocabularySqLite != null) {
			for (int index = 0; index < vocabulary.length(); ++index) {
				Cursor cursor = null;
				
				try {
					StringBuilder sbSQL = new StringBuilder();
					sbSQL.append("SELECT CHARACTER, ")
					     .append("       SOUND_READ, ")
					     .append("       MEAN_READ, ")
					     .append("       JLPT_CLASS, ")
					     .append("       TRANSLATION ")
					     .append("  FROM TBL_HANJA ")
					     .append(" WHERE CHARACTER='").append(vocabulary.charAt(index)).append("' ")
					     .append(" LIMIT 1");

					cursor = mJvVocabularySqLite.rawQuery(sbSQL.toString(), null);

					if (cursor.moveToFirst() == true) {
						if (sbResult.length() > 0)
							sbResult.append("\n\n");

						if (cursor.getLong(3/* JLPT_CLASS */) == 99) {
							sbResult.append(cursor.getString(0/* CHARACTER */))
								    .append("\n")
								    .append(cursor.getString(4/* TRANSLATION */))
								    .append("\n음독: ")
								    .append(cursor.getString(1/* SOUND_READ */))
								    .append("\n훈독: ")
								    .append(cursor.getString(2/* MEAN_READ */));
						} else {
							sbResult.append(cursor.getString(0/* CHARACTER */))
								    .append(" (JLPT ")
								    .append(cursor.getLong(3/* JLPT_CLASS */))
								    .append("급)\n")
								    .append(cursor.getString(4/* TRANSLATION */))
								    .append("\n음독: ")
								    .append(cursor.getString(1/* SOUND_READ */))
								    .append("\n훈독: ")
								    .append(cursor.getString(2/* MEAN_READ */));
						}
					}
				} catch (SQLiteException e) {
					Log.e(TAG, e.getMessage());
				} finally {
					if (cursor != null)
						cursor.close();
				}
			}
		}
		
		return sbResult.toString();
	}

}
