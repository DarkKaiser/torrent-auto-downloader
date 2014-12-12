package kr.co.darkkaiser.jv.vocabulary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.view.list.SearchListCondition;
import kr.co.darkkaiser.jv.vocabulary.db.UserDbSQLiteOpenHelper;
import kr.co.darkkaiser.jv.vocabulary.db.VocabularyDbHelper;

public class VocabularyManager {

	private static final String TAG = "VocabularyManager";

	private static VocabularyManager mInstance = null;

    private SQLiteDatabase mUserDatabase = null;
    private SQLiteDatabase mVocabularyDatabase = null;

    // 전체 단어리스트 테이블
	private Hashtable<Long/* DB인덱스 */, Vocabulary/* 단어 */> mVocabularyTable = new Hashtable<Long, Vocabulary>();

	static {
		mInstance = new VocabularyManager();
	}

	private VocabularyManager() {

	}

	public static VocabularyManager getInstance() {
		return mInstance;
	}

    /**
     * DB에서 단어 데이터를 모두 읽어들입니다.
     */
	@SuppressWarnings("StringBufferReplaceableByString")
    public synchronized boolean initDataFromDB(Context context) {
		assert context != null;

        UserDbSQLiteOpenHelper userDbSQLiteOpenHelper = new UserDbSQLiteOpenHelper(context, Constants.USER_DB_FILENAME_V3, null, 1);

        // 이전에 등록된 모든 단어를 제거한다.
        if (mVocabularyTable.isEmpty() == false)
            mVocabularyTable.clear();

		Cursor cursor = null;

        // 단어 데이터를 읽어들인다.
		try {
			if (mVocabularyDatabase != null) {
				mVocabularyDatabase.close();
				mVocabularyDatabase = null;
			}

			mVocabularyDatabase = SQLiteDatabase.openDatabase(VocabularyDbHelper.getInstance().getVocabularyDbFilePath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);

            StringBuilder sbSQL = new StringBuilder();
            sbSQL.append("  SELECT IDX, VOCABULARY, VOCABULARY_GANA, VOCABULARY_TRANSLATION, INPUT_DATE ")
			     .append("    FROM TBL_VOCABULARY ")
			     .append("   WHERE USE_YN = 'Y' ");

			cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

			if (cursor.moveToFirst() == true) {
				do
				{
					long idx = cursor.getLong(0/* IDX */);
					
		    		mVocabularyTable.put(idx, new Vocabulary(idx,
                            cursor.getLong(4/* INPUT_DATE */),
                            cursor.getString(1/* VOCABULARY */),
                            cursor.getString(2/* VOCABULARY_GANA */),
                            cursor.getString(3/* VOCABULARY_TRANSLATION */)));
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }

        // 사용자 암기정보 데이터를 읽어들인다.
        try {
            if (mUserDatabase != null) {
                mUserDatabase.close();
                mUserDatabase = null;
            }

            mUserDatabase = userDbSQLiteOpenHelper.getWritableDatabase();

            // 이전 버전의 사용자의 암기정보를 저장한 DB파일이 존재하는지 확인하여, 존재하는경우 새로운 버전으로 업그레이드한다.
            upgradeUserDbFile(context);

            // 사용자의 암기정보를 읽어들인다.
            StringBuilder sbSQL = new StringBuilder();
            sbSQL.append("  SELECT V_IDX, MEMORIZE_TARGET, MEMORIZE_COMPLETED, MEMORIZE_COMPLETED_COUNT ")
                 .append("    FROM TBL_USER_VOCABULARY ");

            cursor = mUserDatabase.rawQuery(sbSQL.toString(), null);

            if (cursor.moveToFirst() == true) {
                do
                {
                    Vocabulary vocabulary = mVocabularyTable.get(cursor.getLong(0/* V_IDX */));
                    if (vocabulary != null) {
                        vocabulary.setMemorizeTarget(cursor.getLong(1/* MEMORIZE_TARGET */) == 1);
                        vocabulary.setMemorizeCompleted(cursor.getLong(2/* MEMORIZE_COMPLETED */) == 1, false);
                        vocabulary.setMemorizeCompletedCount(cursor.getLong(3/* MEMORIZE_COMPLETED_COUNT */));
                    } else {
                        assert false;
                    }
                } while (cursor.moveToNext());
            }
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
        } finally {
            if (cursor != null)
                cursor.close();
		}

        return true;
	}

    /**
     * 이전 버전의 사용자의 암기정보를 저장한 DB파일이 존재하는지 확인하여, 존재하는경우 최신버전으로 업그레이드한다.
     */
	@SuppressWarnings("ResultOfMethodCallIgnored")
    private void upgradeUserDbFile(Context context) {
		assert context != null;
        assert mUserDatabase != null;

        //
        // V2 => V3로 마이그레이션한다.
        //
        String v2UserDbFilePath = context.getDatabasePath(Constants.USER_DB_FILENAME_V2).getAbsolutePath();
        File file = new File(v2UserDbFilePath);
        if (file.exists() == true) {
            Cursor cursor = null;
            long userVocabularyCount = 0;

            try {
                cursor = mUserDatabase.rawQuery("SELECT COUNT(*) FROM TBL_USER_VOCABULARY", null);
                if (cursor.moveToFirst() == true)
                    userVocabularyCount = cursor.getLong(0);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            // TBL_USER_VOCABULARY 테이블에 데이터가 없는(최초 1회) 경우에만 마이그레이션 하도록 한다.
            if (userVocabularyCount == 0) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));

                    String line;
                    while ((line = br.readLine()) != null) {
                        StringTokenizer token = new StringTokenizer(line, "|");

                        if (token.countTokens() == 4) {
                            ContentValues values = new ContentValues();
                            values.put("V_IDX", Long.parseLong(token.nextToken()));
                            values.put("MEMORIZE_COMPLETED_COUNT", Long.parseLong(token.nextToken()));
                            values.put("MEMORIZE_TARGET", Long.parseLong(token.nextToken()) == 1 ? 1 : 0);
                            values.put("MEMORIZE_COMPLETED", Long.parseLong(token.nextToken()) == 1 ? 1 : 0);
                            mUserDatabase.insert("TBL_USER_VOCABULARY", null, values);
                        }
                    }

                    br.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            // 이전버전의 파일을 삭제한다.
            try {
                file.delete();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * 주어진 조회조건을 이용하여 단어를 검색합니다.
     */
    public synchronized void searchVocabulary(Context context, SearchListCondition searchListCondition, ArrayList<Vocabulary> vocabularyList) {
		assert context != null;
        assert vocabularyList != null;
        assert searchListCondition != null;

        if (mVocabularyDatabase == null) {
            assert false;
            return;
        }

        Cursor cursor = null;

        try {
            String searchWord = searchListCondition.getSearchWord().trim();
            SearchListCondition.MemorizeTarget memorizeTarget = searchListCondition.getMemorizeTarget();
            SearchListCondition.MemorizeCompleted memorizeCompleted = searchListCondition.getMemorizeCompleted();
			ArrayList<Integer> jlptRankingSelectedIndicies = searchListCondition.getJLPTRankingSelectedIndicies();

            // 선택된 JLPT 급수가 없다면 검색을 해도 검색결과가 없을 것이므로 바로 반환한다.
            if (jlptRankingSelectedIndicies.size() == 0)
                return;

            boolean mustDbSelection = false;
            StringBuilder sbSQL = new StringBuilder();
            SearchListCondition.JLPTRanking[] jlptRankingValues = SearchListCondition.JLPTRanking.values();

            if (jlptRankingValues.length == jlptRankingSelectedIndicies.size()) {
                sbSQL.append("SELECT V.IDX ")
                     .append("  FROM TBL_VOCABULARY V ")
                     .append(" WHERE 1=1 ");
            } else {
                mustDbSelection = true;

                sbSQL.append("SELECT V.IDX ")
                     .append("  FROM TBL_VOCABULARY V ")
                     .append(" WHERE V.IDX IN (  SELECT DISTINCT V_IDX ")
                     .append("                     FROM TBL_VOCABULARY_JLPT_CLASS_MAPP ")
                     .append("                    WHERE 1=1 ")
                     .append("                      AND CODE_ID IN (");

                boolean firstOne = true;
                for (Integer ordinal : jlptRankingSelectedIndicies) {
                    if (firstOne == false)
                        sbSQL.append(", ");

                    firstOne = false;
                    sbSQL.append("'").append(SearchListCondition.JLPTRanking.parseJLPTRanking(ordinal).getCode()).append("'");
                }

                sbSQL.append(" ) ) ");
            }

            // '단어 뜻' 검색 조건 추가
            if (TextUtils.isEmpty(searchWord) == false) {
                mustDbSelection = true;
                sbSQL.append(" AND V.VOCABULARY_TRANSLATION LIKE '%").append(searchWord).append("%' ");
            }

            ArrayList<Long> idxList = new ArrayList<Long>();

            if (mustDbSelection == true) {
                cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

                if (cursor.moveToFirst() == true) {
                    do
                    {
                        idxList.add(cursor.getLong(0/* IDX */));
                    } while (cursor.moveToNext());
                }

                // 현재까지의 검색결과가 없다면 앞으로 더 검색해봐야 의미 없으므로 바로 반환한다.
                if (idxList.isEmpty() == true)
                    return;

                // '암기완료', '암기대상'의 검색조건이 모든 단어를 대상으로 하면 현재까지의 검색결과를 반환한다.
                if (memorizeTarget == SearchListCondition.MemorizeTarget.ALL && memorizeCompleted == SearchListCondition.MemorizeCompleted.ALL) {
                    for (Long idx : idxList)
                        vocabularyList.add(mVocabularyTable.get(idx));

                    return;
                }

                boolean isMemorizeTarget = false;
                boolean isMemorizeCompleted = false;
                if (memorizeTarget == SearchListCondition.MemorizeTarget.MEMORIZE_TARGET)
                    isMemorizeTarget = true;
                if (memorizeCompleted == SearchListCondition.MemorizeCompleted.MEMORIZE_COMPLETED)
                    isMemorizeCompleted = true;

                for (Long idx : idxList) {
                    Vocabulary vocabulary = mVocabularyTable.get(idx);
                    if (memorizeTarget != SearchListCondition.MemorizeTarget.ALL && vocabulary.isMemorizeTarget() != isMemorizeTarget)
                        continue;
                    if (memorizeCompleted != SearchListCondition.MemorizeCompleted.ALL && vocabulary.isMemorizeCompleted() != isMemorizeCompleted)
                        continue;

                    vocabularyList.add(vocabulary);
                }
            } else {
                // '암기완료', '암기대상'의 검색조건이 모든 단어를 대상으로 하면 전체단어를 반환한다.
                if (memorizeTarget == SearchListCondition.MemorizeTarget.ALL && memorizeCompleted == SearchListCondition.MemorizeCompleted.ALL) {
                    for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); )
                        vocabularyList.add(e.nextElement());

                    return;
                }

                boolean isMemorizeTarget = false;
                boolean isMemorizeCompleted = false;
                if (memorizeTarget == SearchListCondition.MemorizeTarget.MEMORIZE_TARGET)
                    isMemorizeTarget = true;
                if (memorizeCompleted == SearchListCondition.MemorizeCompleted.MEMORIZE_COMPLETED)
                    isMemorizeCompleted = true;

                for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
                    Vocabulary vocabulary = e.nextElement();
                    if (memorizeTarget != SearchListCondition.MemorizeTarget.ALL && vocabulary.isMemorizeTarget() != isMemorizeTarget)
                        continue;
                    if (memorizeCompleted != SearchListCondition.MemorizeCompleted.ALL && vocabulary.isMemorizeCompleted() != isMemorizeCompleted)
                        continue;

                    vocabularyList.add(vocabulary);
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * 단어를 반화합니다.
     */
    public synchronized Vocabulary getVocabulary(long idx) {
		return mVocabularyTable.get(idx);
	}

    /**
     * 암기대상 단어리스트 및 암기대상 단어리스트중에서의 암기완료 단어 갯수를 반환합니다.
     *
     * @param vocabularyList 암기대상 단어리스트
     * @return 암기완료 단어 갯수
     */
    public synchronized int getMemorizeTargetVocabularyList(ArrayList<Vocabulary> vocabularyList) {
        assert vocabularyList != null;

		int memorizeCompletedCount = 0;
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary vocabulary = e.nextElement();
			if (vocabulary.isMemorizeTarget() == true) {
				vocabularyList.add(vocabulary);

				if (vocabulary.isMemorizeCompleted() == true)
					++memorizeCompletedCount;
			}
		}

		return memorizeCompletedCount;
	}

    /**
     * 단어에 대한 상세설명을 반환합니다.
     *
     * @param vocabulary 단어 객체
     * @return 단어에 대한 상세설명
     */
    @SuppressWarnings("StringBufferReplaceableByString")
    public synchronized String getVocabularyDetailDescription(Vocabulary vocabulary) {
        assert vocabulary != null;

		StringBuilder sbResult = new StringBuilder();
		if (mVocabularyDatabase != null) {
            String vocabularyHanja = vocabulary.getVocabulary();
			for (int index = 0; index < vocabularyHanja.length(); ++index) {
				Cursor cursor = null;

				try {
					StringBuilder sbSQL = new StringBuilder();
					sbSQL.append("SELECT CHARACTER, SOUND_READ, MEAN_READ, TRANSLATION ")
					     .append("  FROM TBL_HANJA ")
					     .append(" WHERE CHARACTER = '").append(vocabularyHanja.charAt(index)).append("' ")
					     .append(" LIMIT 1");

					cursor = mVocabularyDatabase.rawQuery(sbSQL.toString(), null);

					if (cursor.moveToFirst() == true) {
						if (sbResult.length() > 0)
							sbResult.append("\n\n");

                        sbResult.append(cursor.getString(0/* CHARACTER */)).append("\n").append(cursor.getString(3/* TRANSLATION */)).append("\n음독: ").append(cursor.getString(1/* SOUND_READ */)).append("\n훈독: ").append(cursor.getString(2/* MEAN_READ */));
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

    /**
     * 단어에 등록된 예문을 반환합니다.
     *
     * @param vocabulary 단어 객체
     * @return 단어에 등록된 예문
     */
    @SuppressWarnings("StringBufferReplaceableByString")
    public synchronized String getVocabularyExample(Vocabulary vocabulary) {
        assert vocabulary != null;

        StringBuilder sbResult = new StringBuilder();

        if (mVocabularyDatabase != null) {
            Cursor cursor = null;

            try {
                StringBuilder sbSQL = new StringBuilder();
				sbSQL.append(" SELECT VOCABULARY, VOCABULARY_TRANSLATION ")
				     .append("   FROM TBL_VOCABULARY_EXAMPLE ")
                     .append("  WHERE IDX IN ( SELECT E_IDX ")
                     .append("                   FROM TBL_VOCABULARY_EXAMPLE_MAPP ")
                     .append("                  WHERE V_IDX = ").append(vocabulary.getIdx())
                     .append("        ) ");

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
		
		if (sbResult.length() == 0)
            sbResult.append("등록된 예문이 없습니다.");
		
		return sbResult.toString();
	}

    /**
     * 전체단어에 대한 단어갯수, 암기대상갯수, 암기완료갯수를 반환합니다.
     *
     * @return 단어갯수, 암기대상갯수, 암기완료갯수
     */
    public synchronized ArrayList<Integer> getVocabularyCountInfo() {
		int memorizeTargetCount = 0;
		int memorizeCompletedCount = 0;
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary vocabulary = e.nextElement();
			if (vocabulary.isMemorizeTarget() == true)
				++memorizeTargetCount;
			if (vocabulary.isMemorizeCompleted() == true)
				++memorizeCompletedCount;
		}

		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(mVocabularyTable.size());
		result.add(memorizeTargetCount);
		result.add(memorizeCompletedCount);

		return result;
	}

    /**
     * 모든 암기대상 단어를 미암기 상태로 설정한다.
     */
    public synchronized void memorizeTargetVocabularyRememorizeAll() {
        for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
            Vocabulary vocabulary = e.nextElement();
            if (vocabulary.isMemorizeTarget() == true) {
                vocabulary.setMemorizeCompleted(false, false);

                // @@@@@ updateUserVocabulary 성능문제
                updateUserVocabulary(vocabulary);
            }
        }
    }

    /**
     * 인자값으로 넘어 온 단어 idx 리스트의 암기대상, 암기완료 여부를 설정합니다.
     *
     * @param menuItemId 메뉴 ID
     * @param excludeSearchVocabularyTargetCancel 검색결과에 포함되지 않은 단어들을 암기대상에서 제외할지의 여부
     * @param idxList 단어 idx 리스트
     */
    public synchronized void memorizeSettingsVocabulary(int menuItemId, boolean excludeSearchVocabularyTargetCancel, ArrayList<Long> idxList) {
        // @@@@@ updateUserVocabulary 성능문제
        if (menuItemId == R.id.avsl_search_result_vocabulary_rememorize_all) {							// 검색된 전체 단어 재암기
            for (Long idx : idxList) {
                Vocabulary vocabulary = mVocabularyTable.get(idx);
                assert vocabulary != null;

                vocabulary.setMemorizeTarget(true);
                vocabulary.setMemorizeCompleted(false, false);

                updateUserVocabulary(vocabulary);
            }
        } else if (menuItemId == R.id.avsl_search_result_vocabulary_memorize_completed_all) {			// 검색된 전체 단어 암기 완료
            for (Long idx : idxList) {
                Vocabulary vocabulary = mVocabularyTable.get(idx);
                assert vocabulary != null;

                vocabulary.setMemorizeCompleted(true, true);

                updateUserVocabulary(vocabulary);
            }
        } else if (menuItemId == R.id.avsl_search_result_vocabulary_memorize_target_all) {				// 검색된 전체 단어 암기 대상 만들기
            if (excludeSearchVocabularyTargetCancel == true) {
                for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
                    Vocabulary vocabulary = e.nextElement();
                    assert vocabulary != null;

                    vocabulary.setMemorizeTarget(false);

                    updateUserVocabulary(vocabulary);
                }
            }

            for (Long idx : idxList) {
                Vocabulary vocabulary = mVocabularyTable.get(idx);
                assert vocabulary != null;

                vocabulary.setMemorizeTarget(true);

                updateUserVocabulary(vocabulary);
            }
        } else if (menuItemId == R.id.avsl_search_result_vocabulary_memorize_target_cancel_all) {		// 검색된 전체 단어 암기 대상 해제
            for (Long idx : idxList) {
                Vocabulary vocabulary = mVocabularyTable.get(idx);
                assert vocabulary != null;

                vocabulary.setMemorizeTarget(false);

                updateUserVocabulary(vocabulary);
            }
        } else {
            assert false;
        }
    }

    /**
     * 해당 단어의 사용자 암기정보를 갱신합니다.
     */
    public synchronized void updateUserVocabulary(Vocabulary vocabulary) {
        assert mUserDatabase != null;

        if (vocabulary == null)
            return;

        ContentValues values = new ContentValues();
        values.put("MEMORIZE_TARGET", vocabulary.isMemorizeTarget() ? "1" : "0");
        values.put("MEMORIZE_COMPLETED", vocabulary.isMemorizeCompleted() ? "1" : "0");
        values.put("MEMORIZE_COMPLETED_COUNT", vocabulary.getMemorizeCompletedCount());
        int updateCount = mUserDatabase.update("TBL_USER_VOCABULARY", values, "V_IDX=?", new String[] { Long.toString(vocabulary.getIdx()) });
        if (updateCount == 0) {
            values.put("V_IDX", vocabulary.getIdx());
            mUserDatabase.insert("TBL_USER_VOCABULARY", null, values);
        }
    }

    /**
     * 단어DB의 업데이트 정보를 반환합니다.
     */
    public synchronized long getVocabularyUpdateInfo(long prevMaxIdx, StringBuilder sb) {
		assert sb != null;

		long newMaxIdx = -1;
		long updateVocabularyCount = 0;
		for (Enumeration<Vocabulary> e = mVocabularyTable.elements(); e.hasMoreElements(); ) {
			Vocabulary vocabulary = e.nextElement();
			if (vocabulary.getIdx() > prevMaxIdx) {
				++updateVocabularyCount;

				if (vocabulary.getIdx() > newMaxIdx)
                    newMaxIdx = vocabulary.getIdx();

				// 최대 200개 이상의 단어는 출력되지 않도록 한다.
				if (updateVocabularyCount < 200) {
					sb.append(vocabulary.getVocabulary())
					  .append("(")
					  .append(vocabulary.getVocabularyGana())
					  .append(") - ")
					  .append(vocabulary.getVocabularyTranslation())
					  .append("\n");					
				}
			}
		}

		if (updateVocabularyCount > 0) {
			sb.insert(0, String.format("%d개의 단어가 갱신되었습니다.\n\n", updateVocabularyCount));

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
