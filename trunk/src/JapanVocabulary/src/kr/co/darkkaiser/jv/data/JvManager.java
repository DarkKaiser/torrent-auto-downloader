package kr.co.darkkaiser.jv.data;

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

import kr.co.darkkaiser.jv.view.list.JvListSearchCondition;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class JvManager {

	private static final String TAG = "JvManager";

	private static JvManager mInstance = null;
	
	private String mJvVocabularyDbPath = null;
	private String mJvUserVocubularyInfoFilePath = null;

	private SQLiteDatabase mJvVocabularySqLite = null;

	/*
	 * ��ü �Ϻ��� �ܾ� ����Ʈ ���̺�
	 */
	private Hashtable<Long, JapanVocabulary> mJvTable = new Hashtable<Long, JapanVocabulary>();

	static {
		mInstance = new JvManager();
	}

	private JvManager() {
		// �����ͺ��̽� ����, ������� �ܾ ���� ������ ������ ������ ��ġ�ϴ� ��θ� ���Ѵ�.
		String appMainPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
		File f = new File(appMainPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		mJvVocabularyDbPath = String.format("%s%s", appMainPath, JvDefines.JV_VOCABULARY_DB);
		mJvUserVocubularyInfoFilePath = String.format("%s%s", appMainPath, JvDefines.JV_USER_VOCABULARY_INFO_FILE);
	}

	public static JvManager getInstance() {
		return mInstance;
	}

	public synchronized boolean initDataFromDB() {
		// ������ ��ϵ� ��� �ܾ �����Ѵ�.
		if (mJvTable.isEmpty() == false)
			mJvTable.clear();

		Cursor cursor = null;

		try {
			if (mJvVocabularySqLite != null) {
				mJvVocabularySqLite.close();
				mJvVocabularySqLite = null;
			}

			// �Ϻ��� �ܾ �о���δ�.
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
		} catch (SQLiteException e) {
			Log.e(TAG, e.getMessage());
			return false;
		} finally {
			if (cursor != null)
				cursor.close();
		}

		// ����� ���Ͽ��� �ܾ� �ϱ⿡ ���� ������ �о���δ�.
		try {
			File f = new File(mJvUserVocubularyInfoFilePath);
			if (f.exists() == true) {
				BufferedReader br = new BufferedReader(new FileReader(f));

				String line = null;
				while ((line = br.readLine()) != null) {
					StringTokenizer token = new StringTokenizer(line, "|");

					if (token.countTokens() == 4) {
						long idx = Long.parseLong(token.nextToken());
						JapanVocabulary jpVocabulary = mJvTable.get(idx);

						if (jpVocabulary != null) {
							jpVocabulary.setFirstOnceMemorizeCompletedCount(Long.parseLong(token.nextToken()));
							jpVocabulary.setMemorizeTarget(Long.parseLong(token.nextToken()) == 1 ? true : false, false);
							jpVocabulary.setMemorizeCompleted(Long.parseLong(token.nextToken()) == 1 ? true : false, false, false);
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

	public synchronized void searchVocabulary(Context context, JvListSearchCondition searchCondition, ArrayList<JapanVocabulary> jvList) {
		assert context != null;

		if (mJvVocabularySqLite != null) {
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

				// 'JLPT �޼�' �˻� ���� �߰�
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
				
					String[] items = context.getResources().getStringArray(R.array.sc_jlpt_level_list_values);
					for (int index = 0; index < checkedItems.length; ++index) {
						if (checkedItems[index] == true) {
							if (index > 0)
								sbSQL.append(", ");
							
							sbSQL.append(items[index]);
						}
					}
					
					sbSQL.append(" ) ) ");
				}

				// '�ܾ� �� �˻���' �˻� ���� �߰�
				if (TextUtils.isEmpty(searchWord) == false) {
					hasSearchCondition = true;
					sbSQL.append(" AND V.VOCABULARY_TRANSLATION LIKE '%").append(searchWord).append("%' ");
				}

				// '�ܾ� �����' �˻� ���� �߰�
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

						lastSearchDateValue += new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime();
						lastSearchDateValue += 999/* �и��� */;

						sbSQL.append(" AND REGISTRATION_DATE >= ").append(firstSearchDateValue)
						 	 .append(" AND REGISTRATION_DATE <= ").append(lastSearchDateValue);
					} catch (ParseException e) {
						Log.e(TAG, e.getMessage());
						return;
					}
				}

				ArrayList<Long> idxList = new ArrayList<Long>();

				if (hasSearchCondition == true) {
					cursor = mJvVocabularySqLite.rawQuery(sbSQL.toString(), null);

					if (cursor.moveToFirst() == true) {
						do
						{
							idxList.add(cursor.getLong(0/* IDX */));
						} while (cursor.moveToNext());
					}

					cursor.close();
					cursor = null;

					// ��������� �˻� ����� ���ٸ� ������ �� �˻��غ��� �ǹ� �����Ƿ� ��ȯ�Ѵ�.
					if (idxList.isEmpty() == true)
						return;

					// '�ϱ�Ϸ�', '�ϱ���'�� �˻� ������ ��� �ܾ������� �ϸ� ��������� �˻� ����� ��ȯ�Ѵ�.
					if (memorizeTargetPosition == 0/*��� �ܾ�*/ && memorizeCompletedPosition == 0/*��� �ܾ�*/) {
						for (int index = 0; index < idxList.size(); ++index)
							jvList.add(mJvTable.get(idxList.get(index)));
						
						return;
					}

					boolean memorizeTarget = false;
					boolean memorizeCompleted = false;
					if (memorizeTargetPosition == 1/*�ϱ� ��� �ܾ�*/)
						memorizeTarget = true;
					if (memorizeCompletedPosition == 1/*�ϱ� �Ϸ�� �ܾ�*/)
						memorizeCompleted = true;

					for (int index = 0; index < idxList.size(); ++index) {
						JapanVocabulary japanVocabulary = mJvTable.get(idxList.get(index));
						if (memorizeTargetPosition != 0/*��� �ܾ�*/ && japanVocabulary.isMemorizeTarget() != memorizeTarget)
							continue;
						if (memorizeCompletedPosition != 0/*��� �ܾ�*/ && japanVocabulary.isMemorizeCompleted() != memorizeCompleted)
							continue;
						
						jvList.add(japanVocabulary);
					}
				} else {
					// '�ϱ�Ϸ�', '�ϱ���'�� �˻� ������ ��� �ܾ������� �ϸ� ��� �ܾ ��ȯ�Ѵ�.
					if (memorizeTargetPosition == 0/*��� �ܾ�*/ && memorizeCompletedPosition == 0/*��� �ܾ�*/) {
						for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); )
							jvList.add(e.nextElement());
						
						return;
					}

					boolean memorizeTarget = false;
					boolean memorizeCompleted = false;
					if (memorizeTargetPosition == 1/*�ϱ� ��� �ܾ�*/)
						memorizeTarget = true;
					if (memorizeCompletedPosition == 1/*�ϱ� �Ϸ�� �ܾ�*/)
						memorizeCompleted = true;

					for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
						JapanVocabulary japanVocabulary = e.nextElement();
						if (memorizeTargetPosition != 0/*��� �ܾ�*/ && japanVocabulary.isMemorizeTarget() != memorizeTarget)
							continue;
						if (memorizeCompletedPosition != 0/*��� �ܾ�*/ && japanVocabulary.isMemorizeCompleted() != memorizeCompleted)
							continue;
						
						jvList.add(japanVocabulary);
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
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true)
				jv.setMemorizeCompleted(false, false, false);
		}
		
		writeUserVocabularyInfo();
	}

	public synchronized void writeUserVocabularyInfo() {
		StringBuilder sb = new StringBuilder();
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jpVocabulary = e.nextElement();

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
			File fileOrg = new File(mJvUserVocubularyInfoFilePath);
			File fileTemp = new File(mJvUserVocubularyInfoFilePath + ".tmp");

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

	public synchronized boolean updateMemorizeField(int menuItemId, boolean notSearchVocabularyTargetCancel, ArrayList<Long> idxList) {
		if (menuItemId == R.id.jvlm_all_rememorize) {							// �˻��� ��ü �ܾ� ��ϱ�
			JapanVocabulary jv = null;
			for (int index = 0; index < idxList.size(); ++index) {
				jv = mJvTable.get(idxList.get(index));

				jv.setMemorizeTarget(true, false);
				jv.setMemorizeCompleted(false, false, false);
			}
		} else if (menuItemId == R.id.jvlm_all_memorize_completed) {			// �˻��� ��ü �ܾ� �ϱ� �Ϸ�
			for (int index = 0; index < idxList.size(); ++index)
				mJvTable.get(idxList.get(index)).setMemorizeCompleted(true, true, false);
		} else if (menuItemId == R.id.jvlm_all_memorize_target) {				// �˻��� ��ü �ܾ� �ϱ� ��� �����
			if (notSearchVocabularyTargetCancel == true) {
				for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
					JapanVocabulary jpVocabulary = e.nextElement();
					jpVocabulary.setMemorizeTarget(false, false);
				}
			}

			for (int index = 0; index < idxList.size(); ++index)
				mJvTable.get(idxList.get(index)).setMemorizeTarget(true, false);
		} else if (menuItemId == R.id.jvlm_all_memorize_target_cancel) {		// �˻��� ��ü �ܾ� �ϱ� ��� ����
			for (int index = 0; index < idxList.size(); ++index)
				mJvTable.get(idxList.get(index)).setMemorizeTarget(false, false);
		}
		
		writeUserVocabularyInfo();

		return true;
	}

	public synchronized String getJapanVocabularyDetailDescription(String vocabulary) {
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
								    .append("\n����: ")
								    .append(cursor.getString(1/* SOUND_READ */))
								    .append("\n�Ƶ�: ")
								    .append(cursor.getString(2/* MEAN_READ */));
						} else {
							sbResult.append(cursor.getString(0/* CHARACTER */))
								    .append(" ( JLPT N")
								    .append(cursor.getLong(3/* JLPT_CLASS */))
								    .append(" )\n")
								    .append(cursor.getString(4/* TRANSLATION */))
								    .append("\n����: ")
								    .append(cursor.getString(1/* SOUND_READ */))
								    .append("\n�Ƶ�: ")
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

	public synchronized ArrayList<Integer> getVocabularyInfo() {
		int memorizeTargetCount = 0;
		int memorizeCompletedCount = 0;
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jv = e.nextElement();
			if (jv.isMemorizeTarget() == true)
				++memorizeTargetCount;
			if (jv.isMemorizeCompleted() == true)
				++memorizeCompletedCount;
		}
		
		ArrayList<Integer> result = new ArrayList<Integer>();

		result.add(mJvTable.size());
		result.add(memorizeTargetCount);
		result.add(memorizeCompletedCount);
		
		return result;
	}

	public synchronized long getUpdatedJapanVocabularyInfo(long prevMaxIdx, StringBuilder sb) {
		assert sb != null;

		long newMaxIdx = -1;
		long updateVocabularyCount = 0;
		for (Enumeration<JapanVocabulary> e = mJvTable.elements(); e.hasMoreElements(); ) {
			JapanVocabulary jpVocabulary = e.nextElement();
			if (jpVocabulary.getIdx() > prevMaxIdx) {
				++updateVocabularyCount;

				if (jpVocabulary.getIdx() > newMaxIdx) {
					newMaxIdx = jpVocabulary.getIdx();
				}

				// �ִ� 200�� �̻��� �ܾ�� ��µ��� �ʵ��� �Ѵ�.
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
			sb.insert(0, String.format("%d���� �ܾ ������Ʈ �Ǿ����ϴ�.\n\n", updateVocabularyCount));

			// 200�� �̻��� �ܾ ������Ʈ �Ǿ��� ��� '.....' ���ڸ� �������� ���̵��� �Ѵ�.
			if (updateVocabularyCount > 200) {
				sb.append(".....");
			} else {
				// �������� �߰��� '\n' ���ڸ� �����Ѵ�.
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		
		return newMaxIdx;
	}

}
