package kr.co.darkkaiser.jv.vocabulary.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.view.list.JvSearchListCondition;

public class VocabularyManager {

	private static final String TAG = "VocabularyManager";

	private static VocabularyManager mInstance = null;

	private SQLiteDatabase mVocabularyDatabase = null;

	/*
	 * 전체 단어리스트 테이블
	 */
	private Hashtable<Long/* 인덱스 */, Vocabulary/* 단어 */> mVocabularyTable = new Hashtable<Long, Vocabulary>();

	static {
		mInstance = new VocabularyManager();
	}

	private VocabularyManager() {

	}

	public static VocabularyManager getInstance() {
		return mInstance;
	}

    // @@@@@
	public synchronized boolean initDataFromDB(Context context) {
		assert context != null;
		
		// 이전에 등록된 모든 단어를 제거한다.
		if (mVocabularyTable.isEmpty() == false)
			mVocabularyTable.clear();

		// 단어 DB 파일이 존재하는지 체크하여 존재하지 않는 경우는 assets에서 복사하도록 한다.
		checkJpVocabularyDatabaseFile(context);

		Cursor cursor = null;

		try {
			if (mVocabularyDatabase != null) {
				mVocabularyDatabase.close();
				mVocabularyDatabase = null;
			}

			// 일본어 단어를 읽어들인다.
			mVocabularyDatabase = SQLiteDatabase.openDatabase(VocabularyDbManager.getInstance().getVocabularyDbFilePath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
			if (mVocabularyDatabase == null)
				return false;

			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("  SELECT IDX, ")
			     .append("         VOCABULARY, ")
			     .append("         VOCABULARY_GANA, ")
			     .append("         VOCABULARY_TRANSLATION, ")
			     .append("         REGISTRATION_DATE ")
			     .append("    FROM TBL_VOCABULARY ");

			cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

			if (cursor.moveToFirst() == true) {
				do
				{
					long idx = cursor.getLong(0/* IDX */);
					
		    		mVocabularyTable.put(idx, new Vocabulary(idx,
                            cursor.getLong(4/* REGISTRATION_DATE */),
                            cursor.getString(1/* VOCABULARY */),
                            cursor.getString(2/* VOCABULARY_GANA */),
                            cursor.getString(3/* VOCABULARY_TRANSLATION */)));
				} while (cursor.moveToNext());
			}

			cursor.close();
			cursor = null;
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			if (cursor != null)
				cursor.close();
		}

		// 사용자 파일에서 단어 암기에 대한 정보를 읽어들인다.
		try {
			File f = new File(VocabularyDbManager.getInstance().getUserDbFilePath());
			if (f.exists() == true) {
				BufferedReader br = new BufferedReader(new FileReader(f));

				String line = null;
				while ((line = br.readLine()) != null) {
					StringTokenizer token = new StringTokenizer(line, "|");

					if (token.countTokens() == 4) {
						long idx = Long.parseLong(token.nextToken());
						Vocabulary vocabulary = mVocabularyTable.get(idx);

						if (vocabulary != null) {
							vocabulary.setFirstOnceMemorizeCompletedCount(Long.parseLong(token.nextToken()));
							vocabulary.setMemorizeTarget(Long.parseLong(token.nextToken()) == 1 ? true : false, false);
							vocabulary.setMemorizeCompleted(Long.parseLong(token.nextToken()) == 1 ? true : false, false, false);
						} else {
							assert false;
						}
					} else {
						assert false;
					}
				}
				
				br.close();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}

        return true;
	}

    // @@@@@
	private void checkJpVocabularyDatabaseFile(Context context) {
		assert context != null;
		assert TextUtils.isEmpty(VocabularyDbManager.getInstance().getVocabularyDbFilePath()) == false;
//@@@@@
//		String jvDbPath = JvPathManager.getInstance().getVocabularyDbFilePath();
//		SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//
//		// 단어 DB 파일이 존재하는지 확인한다.
//		File f = new File(jvDbPath);
//		if (f.exists() == true) {
//			// 최초 혹은 업데이트로 재설치되는 경우 단어 DB 파일이 존재할 때 단어 버전을 다시 한번 확인한다.
//			String jvDbVersion = preferences.getString(Constants.SP_DB_VERSION, "");
//			if (jvDbVersion.equals("") == false) {
//				int currentDbVersion = Integer.parseInt(jvDbVersion.substring(Constants.JV_DB_VERSION_PREFIX.length()));
//				int assetsDbVersion = Integer.parseInt(Constants.JV_DB_VERSION_FROM_ASSETS.substring(Constants.JV_DB_VERSION_PREFIX.length()));
//
//				if (currentDbVersion >= assetsDbVersion) {
//					return;
//				}
//			}
//		}
	}

    // @@@@@
    public synchronized void searchVocabulary(Context context, JvSearchListCondition searchCondition, ArrayList<Vocabulary> jvList) {
		assert context != null;

		if (mVocabularyDatabase != null) {
			Cursor cursor = null;

			try {
				String searchWord = searchCondition.getSearchWord().trim();
				int memorizeTargetPosition = searchCondition.getMemorizeTargetPosition();
				int memorizeCompletedPosition = searchCondition.getMemorizeCompletedPosition();
				boolean allRegDateSearch = searchCondition.isAllRegDateSearch();
				String firstSearchDate = searchCondition.getFirstSearchDate();
				String lastSearchDate = searchCondition.getLastSearchDate();
				boolean[] checkedItems = searchCondition.getCheckedJLPTLevelArray();

				boolean hasSearchCondition = false;
				StringBuilder sbSQL = new StringBuilder();

				// 'JLPT 급수' 검색 조건 추가
				boolean allSearchJLPTLevel = true;
				for (int index = 1; index < checkedItems.length; ++index) {
					if (checkedItems[0] != checkedItems[index]) {
						allSearchJLPTLevel = false;
						break;
					}
				}
				
				if (allSearchJLPTLevel == true) {
					sbSQL.append("SELECT V.IDX ")
				 	 	 .append("  FROM TBL_VOCABULARY AS V ")
				 	 	 .append(" WHERE 1=1 ");
				} else {
					hasSearchCondition = true;

					sbSQL.append("SELECT V.IDX ")
						 .append("  FROM TBL_VOCABULARY AS V ")
						 .append(" WHERE V.IDX IN (          SELECT DISTINCT V2.IDX ")
						 .append("                             FROM TBL_HANJA AS A ")
						 .append("                  LEFT OUTER JOIN TBL_VOCABULARY AS V2 ")
						 .append("                               ON V2.VOCABULARY LIKE ('%'||A.CHARACTER||'%') ")
						 .append("                            WHERE 1=1 ")
						 .append("                              AND A.JLPT_CLASS IN (");

					boolean isAppended = false;
					String[] items = context.getResources().getStringArray(R.array.sc_jlpt_level_list_values);
					for (int index = 0; index < checkedItems.length; ++index) {
						if (checkedItems[index] == true) {
							if (isAppended == true)
								sbSQL.append(", ");
							
							isAppended = true;
							sbSQL.append(items[index]);
						}
					}
					
					sbSQL.append(" ) ) ");
				}

				// '단어 뜻 검색어' 검색 조건 추가
				if (TextUtils.isEmpty(searchWord) == false) {
					hasSearchCondition = true;
					sbSQL.append(" AND V.VOCABULARY_TRANSLATION LIKE '%").append(searchWord).append("%' ");
				}

				// '단어 등록일' 검색 조건 추가
				if (allRegDateSearch == false) {
					try {
						hasSearchCondition = true;
						long firstSearchDateValue = new SimpleDateFormat("yyyy/MM/dd").parse(firstSearchDate).getTime();
						long lastSearchDateValue = new SimpleDateFormat("yyyy/MM/dd").parse(lastSearchDate).getTime();

						if (firstSearchDateValue > lastSearchDateValue) {
							long temp = firstSearchDateValue;
							firstSearchDateValue = lastSearchDateValue;
							lastSearchDateValue = temp;
						}
						
						// 검색 종료일에 검색 종료일의 마지막 시간값을 더한다. 
						lastSearchDateValue += (86400000 - 1);

						sbSQL.append(" AND REGISTRATION_DATE >= ").append(firstSearchDateValue)
						 	 .append(" AND REGISTRATION_DATE <= ").append(lastSearchDateValue);
					} catch (ParseException e) {
						Log.e(TAG, e.getMessage());
						return;
					}
				}

				ArrayList<Long> idxList = new ArrayList<Long>();

				if (hasSearchCondition == true) {
					cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

					if (cursor.moveToFirst() == true) {
						do
						{
							idxList.add(cursor.getLong(0/* IDX */));
						} while (cursor.moveToNext());
					}

					cursor.close();
					cursor = null;

					// 현재까지의 검색 결과가 없다면 앞으로 더 검색해봐야 의미 없으므로 반환한다.
					if (idxList.isEmpty() == true)
						return;

					// '암기완료', '암기대상'의 검색 조건이 모든 단어를대상으로 하면 현재까지의 검색 결과를 반환한다.
					if (memorizeTargetPosition == 0/*모든 단어*/ && memorizeCompletedPosition == 0/*모든 단어*/) {
						for (int index = 0; index < idxList.size(); ++index)
							jvList.add(mVocabularyTable.get(idxList.get(index)));
						
						return;
					}

					boolean memorizeTarget = false;
					boolean memorizeCompleted = false;
					if (memorizeTargetPosition == 1/*암기 대상 단어*/)
						memorizeTarget = true;
					if (memorizeCompletedPosition == 1/*암기 완료된 단어*/)
						memorizeCompleted = true;

					for (int index = 0; index < idxList.size(); ++index) {
						Vocabulary vocabulary = mVocabularyTable.get(idxList.get(index));
						if (memorizeTargetPosition != 0/*모든 단어*/ && vocabulary.isMemorizeTarget() != memorizeTarget)
							continue;
						if (memorizeCompletedPosition != 0/*모든 단어*/ && vocabulary.isMemorizeCompleted() != memorizeCompleted)
							continue;
						
						jvList.add(vocabulary);
					}
				} else {
					// '암기완료', '암기대상'의 검색 조건이 모든 단어를대상으로 하면 모든 단어를 반환한다.
					if (memorizeTargetPosition == 0/*모든 단어*/ && memorizeCompletedPosition == 0/*모든 단어*/) {
						for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); )
							jvList.add(e.nextElement());
						
						return;
					}

					boolean memorizeTarget = false;
					boolean memorizeCompleted = false;
					if (memorizeTargetPosition == 1/*암기 대상 단어*/)
						memorizeTarget = true;
					if (memorizeCompletedPosition == 1/*암기 완료된 단어*/)
						memorizeCompleted = true;

					for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
						Vocabulary vocabulary = e.nextElement();
						if (memorizeTargetPosition != 0/*모든 단어*/ && vocabulary.isMemorizeTarget() != memorizeTarget)
							continue;
						if (memorizeCompletedPosition != 0/*모든 단어*/ && vocabulary.isMemorizeCompleted() != memorizeCompleted)
							continue;
						
						jvList.add(vocabulary);
					}
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

    public synchronized Vocabulary getVocabulary(long idx) {
		return mVocabularyTable.get(idx);
	}

    // @@@@@
    public synchronized int getMemorizeTargetJvList(ArrayList<Vocabulary> jvList) {
		int mJvMemorizeCompletedCount = 0;
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary jpVocabulary = e.nextElement();
			if (jpVocabulary.isMemorizeTarget() == true) {
				jvList.add(jpVocabulary);

				if (jpVocabulary.isMemorizeCompleted() == true)
					++mJvMemorizeCompletedCount;
			}
		}

		return mJvMemorizeCompletedCount;
	}

    // @@@@@
    public synchronized void rememorizeAllMemorizeTarget() {
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true)
				jv.setMemorizeCompleted(false, false, false);
		}

		writeUserVocabularyInfo();
	}

    // @@@@@
    public synchronized void writeUserVocabularyInfo() {
		StringBuilder sb = new StringBuilder();
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary jpVocabulary = e.nextElement();

			sb.append(jpVocabulary.getIdx())
			  .append("|")
			  .append(jpVocabulary.getMemorizeCompletedCount())
			  .append("|")
			  .append(jpVocabulary.isMemorizeTarget() == true ? 1 : 0)
			  .append("|")
			  .append(jpVocabulary.isMemorizeCompleted() == true ? 1 : 0)
			  .append("\n");
		}

		try {
			String jvUserVocabularyInfoFilePath = VocabularyDbManager.getInstance().getUserDbFilePath();

			File fileOrg = new File(jvUserVocabularyInfoFilePath);
			File fileTemp = new File(jvUserVocabularyInfoFilePath + ".tmp");

			FileOutputStream fos = new FileOutputStream(fileTemp);
			fos.write(sb.toString().getBytes());
			fos.close();

			if (fileOrg.exists() == true)
				fileOrg.delete();

			fileTemp.renameTo(fileOrg);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

    // @@@@@
    public synchronized boolean updateMemorizeField(int menuItemId, boolean notSearchVocabularyTargetCancel, ArrayList<Long> idxList) {
		if (menuItemId == R.id.jvlm_all_rememorize) {							// 검색된 전체 단어 재암기
			Vocabulary jv = null;
			for (int index = 0; index < idxList.size(); ++index) {
				jv = mVocabularyTable.get(idxList.get(index));

				jv.setMemorizeTarget(true, false);
				jv.setMemorizeCompleted(false, false, false);
			}
		} else if (menuItemId == R.id.jvlm_all_memorize_completed) {			// 검색된 전체 단어 암기 완료
			for (int index = 0; index < idxList.size(); ++index)
				mVocabularyTable.get(idxList.get(index)).setMemorizeCompleted(true, true, false);
		} else if (menuItemId == R.id.jvlm_all_memorize_target) {				// 검색된 전체 단어 암기 대상 만들기
			if (notSearchVocabularyTargetCancel == true) {
				for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
					Vocabulary jpVocabulary = e.nextElement();
					jpVocabulary.setMemorizeTarget(false, false);
				}
			}

			for (int index = 0; index < idxList.size(); ++index)
				mVocabularyTable.get(idxList.get(index)).setMemorizeTarget(true, false);
		} else if (menuItemId == R.id.jvlm_all_memorize_target_cancel) {		// 검색된 전체 단어 암기 대상 해제
			for (int index = 0; index < idxList.size(); ++index)
				mVocabularyTable.get(idxList.get(index)).setMemorizeTarget(false, false);
		}
		
		writeUserVocabularyInfo();

		return true;
	}

    // @@@@@
    public synchronized String getVocabularyDetailDescription(Vocabulary vocabulary) {
        String voc;
        voc = vocabulary.getVocabulary();

		StringBuilder sbResult = new StringBuilder();
		if (mVocabularyDatabase != null) {
			for (int index = 0; index < voc.length(); ++index) {
				Cursor cursor = null;

				try {
					StringBuilder sbSQL = new StringBuilder();
					sbSQL.append("SELECT CHARACTER, ")
					     .append("       SOUND_READ, ")
					     .append("       MEAN_READ, ")
					     .append("       JLPT_CLASS, ")
					     .append("       TRANSLATION ")
					     .append("  FROM TBL_HANJA ")
					     .append(" WHERE CHARACTER='").append(voc.charAt(index)).append("' ")
					     .append(" LIMIT 1");

					cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

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

    // @@@@@
    public synchronized String getVocabularyExample(Vocabulary vocabulary) {
        long idx = vocabulary.getIdx();

		StringBuilder sbResult = new StringBuilder();
		if (mVocabularyDatabase != null) {
			Cursor cursor = null;
			
			try {
				StringBuilder sbSQL = new StringBuilder();
				sbSQL.append("SELECT VOCABULARY, ")
				     .append("       VOCABULARY_TRANSLATION ")
				     .append("  FROM TBL_VOCABULARY_EXAMPLE ")
				     .append(" WHERE V_IDX=").append(idx);

				cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

				if (cursor.moveToFirst() == true) {
					do
					{
						if (sbResult.length() > 0)
							sbResult.append("<br><br>");
						
						sbResult.append(cursor.getString(0/* VOCABULARY */)).append("<br>").append(cursor.getString(1/* VOCABULARY_TRANSLATION */));
					} while (cursor.moveToNext());
				}
			} catch (SQLiteException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				if (cursor != null)
					cursor.close();
			}
		}
		
		if (sbResult.length() == 0) {
			sbResult.append("등록된 예문이 없습니다.");
		}
		
		return sbResult.toString();
	}

    // @@@@@
    public synchronized ArrayList<Integer> getVocabularyInfo() {
		int memorizeTargetCount = 0;
		int memorizeCompletedCount = 0;
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true)
				++memorizeTargetCount;
			if (jv.isMemorizeCompleted() == true)
				++memorizeCompletedCount;
		}
		
		ArrayList<Integer> result = new ArrayList<Integer>();

		result.add(mVocabularyTable.size());
		result.add(memorizeTargetCount);
		result.add(memorizeCompletedCount);
		
		return result;
	}

    // @@@@@
    public synchronized long getUpdatedVocabularyInfo(long prevMaxIdx, StringBuilder sb) {
		assert sb != null;

		long newMaxIdx = -1;
		long updateVocabularyCount = 0;
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary jpVocabulary = e.nextElement();
			if (jpVocabulary.getIdx() > prevMaxIdx) {
				++updateVocabularyCount;

				if (jpVocabulary.getIdx() > newMaxIdx) {
					newMaxIdx = jpVocabulary.getIdx();
				}

				// 최대 200개 이상의 단어는 출력되지 않도록 한다.
				if (updateVocabularyCount < 200) {
					sb.append(jpVocabulary.getVocabulary())
					  .append("(")
					  .append(jpVocabulary.getVocabularyGana())
					  .append(") - ")
					  .append(jpVocabulary.getVocabularyTranslation())
					  .append("\n");					
				}
			}
		}

		if (updateVocabularyCount > 0) {
			sb.insert(0, String.format("%d개의 단어가 추가되었습니다.\n\n", updateVocabularyCount));

			// 200개 이상의 단어가 업데이트 되었을 경우 '.....' 문자를 마지막에 보이도록 한다.
			if (updateVocabularyCount > 200) {
				sb.append(".....");
			} else {
				// 마지막에 추가된 '\n' 문자를 제거한다.
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		
		return newMaxIdx;
	}

}
