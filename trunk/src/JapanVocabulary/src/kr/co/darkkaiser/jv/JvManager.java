package kr.co.darkkaiser.jv;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import kr.co.darkkaiser.jv.list.JvListSearchCondition;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class JvManager {

	private static final String TAG = "JvManager";

	private static JvManager mInstance = null;

	private String mJvUserDbPath = null;
	private SQLiteDatabase mJvUserSqLite = null;

	private String mJvVocabularyDbPath = null;
	private SQLiteDatabase mJvVocabularySqLite = null;

	/*
	 * 전체 일본어 단어 리스트 테이블
	 */
	private Hashtable<Long, JapanVocabulary> mJvTable = new Hashtable<Long, JapanVocabulary>();

	static {
		mInstance = new JvManager();
	}

	private JvManager() {
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

		Cursor cursor = null;

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
			
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("  SELECT IDX, ")
			     .append("         VOCABULARY, ")
			     .append("         VOCABULARY_GANA, ")
			     .append("         VOCABULARY_TRANSLATION, ")
			     .append("         REGISTRATION_DATE ")
			     .append("    FROM TBL_VOCABULARY ");

			cursor = mJvVocabularySqLite.rawQuery(sbSQL.toString(), null);

			if (cursor.moveToFirst() == true) {
				do
				{
					long idx = cursor.getLong(0/* IDX */);
					
		    		mJvTable.put(idx, new JapanVocabulary(idx,
							  cursor.getLong(4/* REGISTRATION_DATE */),
							  cursor.getString(1/* VOCABULARY */),
							  cursor.getString(2/* VOCABULARY_GANA */),
							  cursor.getString(3/* VOCABULARY_TRANSLATION */)));
				} while (cursor.moveToNext());
			}

			cursor.close();
			cursor = null;

			// 사용자 DB에서 단어 암기에 대한 정보를 읽어들인다.
			assert TextUtils.isEmpty(mJvUserDbPath) == false;
			mJvUserSqLite = SQLiteDatabase.openDatabase(mJvUserDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mJvUserSqLite != null) {
				// 테이블이 생성되어 있는지 확인한다.
				sbSQL = new StringBuilder();
				sbSQL.append("  SELECT name FROM (    SELECT * ")
					 .append("                          FROM sqlite_master")
					 .append("                     UNION ALL ")
					 .append("                        SELECT * ")
					 .append("                          FROM sqlite_temp_master ) ")
					 .append("   WHERE type='table' ")
					 .append("ORDER BY name");
				
				cursor = mJvUserSqLite.rawQuery(sbSQL.toString(), null);

				boolean isTableCreated = false;
				if (cursor.moveToFirst() == true) {
					do
					{
						if (TextUtils.equals(cursor.getString(0/*name*/), "TBL_USER_VOCABULARY") == true) {
							isTableCreated = true;
							break;
						}
					} while (cursor.moveToNext());
				}
				
				cursor.close();
				cursor = null;
				
				if (isTableCreated == true) {
					sbSQL = new StringBuilder();
					sbSQL.append("SELECT V_IDX, ")
						 .append("       MEMORIZE_TARGET, ")
						 .append("       MEMORIZE_COMPLETED, ")
						 .append("       MEMORIZE_COMPLETED_COUNT ")
						 .append("  FROM TBL_USER_VOCABULARY ");
	
					cursor = mJvUserSqLite.rawQuery(sbSQL.toString(), null);
	
					if (cursor.moveToFirst() == true) {
						do
						{
							long idx = cursor.getLong(0/* V_IDX */);
							JapanVocabulary japanVocabulary = mJvTable.get(idx);
	
							if (japanVocabulary != null) {
								japanVocabulary.setFirstOnceMemorizeCompletedCount(cursor.getLong(3/* MEMORIZE_COMPLETED_COUNT */));
								japanVocabulary.setMemorizeTarget(cursor.getLong(1/* MEMORIZE_TARGET */) == 1 ? true : false, false);
								japanVocabulary.setMemorizeCompleted(cursor.getLong(2/* MEMORIZE_COMPLETED */) == 1 ? true : false, false, false);
							} else {
								assert false;
							}
						} while (cursor.moveToNext());
					}
	
					cursor.close();
					cursor = null;					
				} else {
					sbSQL = new StringBuilder();
					sbSQL.append("CREATE TABLE TBL_USER_VOCABULARY ( ")
						 .append("    V_IDX                       INTEGER PRIMARY KEY NOT NULL UNIQUE, ")
						 .append("    MEMORIZE_TARGET             INTEGER DEFAULT (0), ")
						 .append("    MEMORIZE_COMPLETED          INTEGER DEFAULT (0), ")
						 .append("    MEMORIZE_COMPLETED_COUNT    INTEGER DEFAULT (0) ")
						 .append(");");

					mJvUserSqLite.execSQL(sbSQL.toString());

					sbSQL = new StringBuilder();
					sbSQL.append("CREATE UNIQUE INDEX TBL_USER_VOCABULARY_INDEX01 ")
						 .append("                 ON TBL_USER_VOCABULARY(V_IDX) ");

					mJvUserSqLite.execSQL(sbSQL.toString());
				}
			}
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			if (cursor != null)
				cursor.close();
		}

        return true;
	}

	// @@@@@
	public void searchVocabulary(JvListSearchCondition searchCondition, ArrayList<JapanVocabulary> jvList) {
		if (mJvVocabularySqLite != null && mJvUserSqLite != null) {
			Cursor cursor = null;

			try {
				String searchWord = searchCondition.getSearchWord().trim();
				int memorizeTargetPosition = searchCondition.getMemorizeTargetPosition();
				int memorizeCompletedPosition = searchCondition.getMemorizeCompletedPosition();
				boolean allRegDateSearch = searchCondition.isAllRegDateSearch();
				String firstSearchDate = searchCondition.getFirstSearchDate();
				String lastSearchDate = searchCondition.getLastSearchDate();

				// JLPT 가 있고 없고에 따라서, 전체 , jlpt 검색일땐 필요없음
				
				
				
				
				
				StringBuilder sbSQL = new StringBuilder();
				sbSQL.append("SELECT IDX ")
					 .append("  FROM TBL_VOCABULARY AS V ")
					 .append(" WHERE 1=1 ");

				// '단어 뜻 검색어' 검색 조건 추가
				if (TextUtils.isEmpty(searchWord) == false)
					 sbSQL.append(" AND V.VOCABULARY_TRANSLATION LIKE '%").append(searchWord).append("%' ");

				// @@@@@
				// '단어 등록일' 검색 조건 추가
				if (allRegDateSearch == false) {
					try {
						long firstSearchDateValue = new SimpleDateFormat("yyyy/MM/dd").parse(firstSearchDate).getTime();
						long lastSearchDateValue = new SimpleDateFormat("yyyy/MM/dd").parse(lastSearchDate).getTime();

						if (firstSearchDateValue > lastSearchDateValue) {
							long temp = firstSearchDateValue;
							firstSearchDateValue = lastSearchDateValue;
							lastSearchDateValue = temp;
						}

						lastSearchDateValue += new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime();
						lastSearchDateValue += 999/* 밀리초 */;

						sbSQL.append(" AND REGISTRATION_DATE >= ").append(firstSearchDateValue).append("AND REGISTRATION_DATE <= ").append(lastSearchDateValue);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				// 'JLPT 급수'  검색 조건 추가
				// @@@@@

				cursor = mJvVocabularySqLite.rawQuery(sbSQL.toString(), null);

				ArrayList<Long> idxList = new ArrayList<Long>();
				if (cursor.moveToFirst() == true) {
					do
					{
						idxList.add(cursor.getLong(0/* IDX */));
					} while (cursor.moveToNext());
				}

				cursor.close();
				cursor = null;
				
				if (idxList.isEmpty() == true)
					return;
					
				if (memorizeTargetPosition == 0 && memorizeCompletedPosition == 0) {
					// @@@@@
					for (int index = 0; index < idxList.size(); ++index)
						jvList.add(mJvTable.get(idxList.get(index)));
					
					return;
				}

				sbSQL = new StringBuilder();
				sbSQL.append("SELECT V_IDX ")
					 .append("  FROM TBL_USER_VOCABULARY ")
					 .append(" WHERE V_IDX IN (");

				for (int index = 0; index < idxList.size(); ++index) {
					if (index > 0)
						sbSQL.append(", ");

					sbSQL.append(idxList.get(index));
				}

				sbSQL.append(") ");

				// '암기 완료'  검색 조건 추가
				if (memorizeCompletedPosition == 1/*암기 완료된 단어*/) {
					sbSQL.append(" AND MEMORIZE_COMPLETED=1 ");
				} else if (memorizeCompletedPosition == 2/*암기 미완료된 단어*/) {
					sbSQL.append(" AND MEMORIZE_COMPLETED=0 ");
				}

				// '암기 대상'  검색 조건 추가
				if (memorizeTargetPosition == 1/*암기 대상 단어*/) {
					sbSQL.append(" AND MEMORIZE_TARGET=1 ");
				} else if (memorizeTargetPosition == 2/*암기 비대상 단어*/) {
					// @@@@@ 아직 추가 안된 단어도 있을 수 있음
					sbSQL.append(" AND MEMORIZE_TARGET=0 ");
				}

				cursor = mJvUserSqLite.rawQuery(sbSQL.toString(), null);

				idxList.clear();
				if (cursor.moveToFirst() == true) {
					do
					{
						idxList.add(cursor.getLong(0/* IDX */));
					} while (cursor.moveToNext());
				}

				cursor.close();
				cursor = null;

				for (int index = 0; index < idxList.size(); ++index) {
					jvList.add(mJvTable.get(idxList.get(index)));					
				}
			} catch (SQLiteException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		} else {
			assert false;
		}
	}

	public synchronized JapanVocabulary getJapanVocabulary(long idx) {
		assert idx >= 0 && idx < mJvTable.size();
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
					 .append(" WHERE MEMORIZE_TARGET=1 ");

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

	// @@@@@ INSERT, UPDATE 속도 개선
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

	// @@@@@ INSERT, UPDATE 속도 개선
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
		if (mJvUserSqLite == null) {
			assert false;
			return false;
		}

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
								    .append(" ( JLPT N")
								    .append(cursor.getLong(3/* JLPT_CLASS */))
								    .append(" )\n")
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
