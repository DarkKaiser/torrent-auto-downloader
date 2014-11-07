package kr.co.darkkaiser.jv.controller.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import kr.co.darkkaiser.jv.common.JvDefines;
import kr.co.darkkaiser.jv.controller.IVocabularyList;
import kr.co.darkkaiser.jv.common.MemorizeTargetItem;
import kr.co.darkkaiser.jv.vocabularydata.JapanVocabulary;
import kr.co.darkkaiser.jv.vocabularydata.JapanVocabularyComparator;
import kr.co.darkkaiser.jv.vocabularydata.JapanVocabularyManager;
import kr.co.darkkaiser.jv.util.CircularBuffer;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

//@@@@@
public class MemorizeVocabularyList implements IVocabularyList {
	
	private Random mRandom = new Random();

	// 암기 대상 단어 리스트
	private ArrayList<JapanVocabulary> mJvList = new ArrayList<JapanVocabulary>();

	// 암기 대상 단어 암기 순서 버퍼
	private CircularBuffer<Integer> mJvListMemorizeSequence = new CircularBuffer<Integer>();

	// 암기 대상 단어 리스트 중에서 암기 완료한 단어의 갯수
	private int mMemorizeCompletedCount = 0;

	// 현재 화면에 보여지고 있는 암기 대상 단어의 위치
	private int mCurrentPosition = -1;

	// (환경설정 값)단어 암기 순서(무작위, 순차(...))
	private int mMemorizeOrderMethod = 0/* 랜덤 */;

	// (환경설정 값)화면에 출력 할 암기 대상 단어의 항목(한자, 히라가나/가타가나)
	private MemorizeTargetItem mJvMemorizeTargetItem = MemorizeTargetItem.VOCABULARY;

	public MemorizeVocabularyList() {
		
	}

	public boolean reloadPreference(SharedPreferences preferences) {
		assert preferences != null;

		// 화면에 출력 할 암기 대상 단어의 항목을 읽는다.
		String memorizeTargetItem = preferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (TextUtils.equals(memorizeTargetItem, "0") == true)
			mJvMemorizeTargetItem = MemorizeTargetItem.VOCABULARY;
		else
			mJvMemorizeTargetItem = MemorizeTargetItem.VOCABULARY_GANA;

		// 단어 암기 순서를 읽는다.
		int prevMemorizeOrderMethod = mMemorizeOrderMethod;
		mMemorizeOrderMethod = Integer.parseInt(preferences.getString(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD, "0"));

		return (mMemorizeOrderMethod != prevMemorizeOrderMethod ? true : false);
	}

	public synchronized void loadData(SharedPreferences preferences, boolean launchApp) {
		// 암기 대상 단어들만을 필터링한다.
		mJvList.clear();
		mCurrentPosition = -1;
		mJvListMemorizeSequence.clear();
		mMemorizeCompletedCount = JapanVocabularyManager.getInstance().getMemorizeTargetJvList(mJvList);

		assert mMemorizeCompletedCount >= 0;
		assert mMemorizeCompletedCount <= mJvList.size();
	
		switch (mMemorizeOrderMethod) {
		case 1:
			Collections.sort(mJvList, JapanVocabularyComparator.mVocabularyComparator);
			break;
		case 3:
			Collections.sort(mJvList, JapanVocabularyComparator.mVocabularyGanaComparator);
			break;
		case 2:
			Collections.sort(mJvList, JapanVocabularyComparator.mVocabularyTranslationComparator);
			break;
		default:
			break;
		}
		
		if (launchApp == true) {
			loadVocabularyPosition(preferences);
		}
	}

	public boolean isValidVocabularyPosition() {
		if (mCurrentPosition < 0)
			return false;
		
		if (mCurrentPosition >= mJvList.size())
			return false;
		
		return true;
	}
	
	public synchronized int getCount() {
		return mJvList.size();
	}

	public synchronized void setMemorizeCompletedAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			JapanVocabulary jpVocabulary = mJvList.get(mCurrentPosition);
			if (jpVocabulary != null && jpVocabulary.isMemorizeCompleted() == false) {
				++mMemorizeCompletedCount;
				jpVocabulary.setMemorizeCompleted(true, true, true);							
			}
		} else {
			assert false;
		}
	}

	public synchronized long getIdxAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			return mJvList.get(mCurrentPosition).getIdx();
		} else {
			assert false;
		}
		
		return -1;
	}

	private void loadVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		int latestMemorizeOrderMethod = preferences.getInt(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, 0/* 랜덤 */);
		if (latestMemorizeOrderMethod == mMemorizeOrderMethod && mMemorizeOrderMethod != 0/* 랜덤 */) {
			int prevCurrentPosition = mCurrentPosition;
			mCurrentPosition = preferences.getInt(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);
			
			if (isValidVocabularyPosition() == false) {
				mCurrentPosition = prevCurrentPosition;
			}
		}
	}

	public void saveVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		Editor edit = preferences.edit();
		edit.putInt(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, mMemorizeOrderMethod);
		if (mMemorizeOrderMethod == 0/* 랜덤 */) {
			edit.putInt(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);
		} else {
			if (mCurrentPosition != -1) {
				edit.putInt(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentPosition - 1);
			} else {
				edit.putInt(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentPosition);
			}			
		}
		
		edit.commit();
	}
	
	public synchronized int getCurrentPosition() {
		return mCurrentPosition;
	}

	@Override
	public synchronized JapanVocabulary getCurrentVocabulary() {
		if (isValidVocabularyPosition() == true) {
			return mJvList.get(mCurrentPosition);
		}

		return null;
	}
	
	public synchronized JapanVocabulary movePosition(int position) {
		int prevCurrentPosition = mCurrentPosition;

		mCurrentPosition = position;
		if (isValidVocabularyPosition() == true) {
			return mJvList.get(mCurrentPosition);
		} else {
			assert false;
			mCurrentPosition = prevCurrentPosition;
		}

		return null;
	}
	
	@Override
	public synchronized JapanVocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		Integer value = mJvListMemorizeSequence.pop();
		if (value != null) {
			int prevCurrentPosition = mCurrentPosition;

			mCurrentPosition = (int)value;
			if (isValidVocabularyPosition() == true) {
				return mJvList.get(mCurrentPosition);
			} else {
				assert false;
				mCurrentPosition = prevCurrentPosition;
			}
		} else {
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized JapanVocabulary nextVocabulary(StringBuilder sbErrMessage) {
		assert sbErrMessage != null;
		
		if (mJvList.isEmpty() == true || mMemorizeCompletedCount >= mJvList.size()) {
			if (mMemorizeOrderMethod == 0/* 랜덤 */) { 
				mCurrentPosition = -1;
			} else {
				mCurrentPosition = mJvList.size() - 1;
			}

			sbErrMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (isValidVocabularyPosition() == true) {
				Integer value = mJvListMemorizeSequence.popNoRemove();
				if (value != null) {
					if (mCurrentPosition != (int)value) {
						mJvListMemorizeSequence.push(mCurrentPosition);
					}
				} else {
					mJvListMemorizeSequence.push(mCurrentPosition);
				}
			}

			if (mMemorizeOrderMethod == 0/* 랜덤 */) {
				int prevCurrentPosition = mCurrentPosition;
				int memorizeUncompletedCount = mJvList.size() - mMemorizeCompletedCount;
				
				do {
					int uncompletedCount = 0;
					int targetUncompletedCount = mRandom.nextInt(memorizeUncompletedCount) + 1;

					for (int index = 0; index < mJvList.size(); ++index) {
						if (mJvList.get(index).isMemorizeCompleted() == false) {
							++uncompletedCount;
							
							if (uncompletedCount == targetUncompletedCount) {
								mCurrentPosition = index;
								break;
							}
						}
					}
				} while (memorizeUncompletedCount > 1 && prevCurrentPosition == mCurrentPosition);
			} else {
				boolean bFindSucceeded = false;
				for (int index = mCurrentPosition + 1; index < mJvList.size(); ++index) {
					if (mJvList.get(index).isMemorizeCompleted() == false) {
						mCurrentPosition = index;
						bFindSucceeded = true;
						break;
					}
				}
				
				if (bFindSucceeded == false) {
					for (int index = 0; index < mCurrentPosition; ++index) {
						if (mJvList.get(index).isMemorizeCompleted() == false) {
							mCurrentPosition = index;
							bFindSucceeded = true;
							break;
						}
					}
				}

				assert bFindSucceeded == true;
			}

			if (isValidVocabularyPosition() == true) {
				return mJvList.get(mCurrentPosition);
			}
		}

		return null;
	}
	
	public MemorizeTargetItem getMemorizeTargetItem() {
		return mJvMemorizeTargetItem;
	}

	public StringBuilder getMemorizeVocabularyInfo() {
		assert isValidVocabularyPosition() == true;

		StringBuilder sb = new StringBuilder();
		sb.append("암기완료 ").append(mMemorizeCompletedCount).append("개 / 전체 ").append(mJvList.size()).append("개");
		return sb;
	}

}
