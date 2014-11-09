package kr.co.darkkaiser.jv.vocabulary.list.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.vocabulary.MemorizeTarget;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;
import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabularyComparator;
import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabularyManager;
import kr.co.darkkaiser.jv.util.CircularBuffer;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

//@@@@@todo
public class MemorizeTargetVocabularyList implements IVocabularyList {
	
	private Random mRandom = new Random();

	// 암기대상 단어 리스트
	private ArrayList<JapanVocabulary> mVocabularyListData = new ArrayList<JapanVocabulary>();

	// 암기대상 단어 암기순서 버퍼
	private CircularBuffer<Integer> mVocabularyListMemorizeSequence = new CircularBuffer<Integer>();

	// 암기대상 단어 리스트 중에서 암기 완료한 단어의 갯수
	private int mMemorizeCompletedCount = 0;

	// 현재 화면에 보여지고 있는 암기 대상 단어의 위치
	private int mCurrentPosition = -1;

	// (환경설정 값)단어 암기 순서(무작위, 순차(...))
	private int mMemorizeOrderMethod = 0/* 랜덤 */;

    // (환경설정 값)화면에 출력 할 암기 대상 단어의 항목(한자, 히라가나/가타가나)
	private MemorizeTarget mMemorizeTarget = MemorizeTarget.VOCABULARY;

	public MemorizeTargetVocabularyList() {
		
	}

	public boolean reloadPreference(SharedPreferences preferences) {
		assert preferences != null;

		// 화면에 출력 할 암기 대상 단어의 항목을 읽는다.
		String memorizeTargetItem = preferences.getString(Constants.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (TextUtils.equals(memorizeTargetItem, "0") == true)
			mMemorizeTarget = MemorizeTarget.VOCABULARY;
		else
			mMemorizeTarget = MemorizeTarget.VOCABULARY_GANA;

		// 단어 암기 순서를 읽는다.
		int prevMemorizeOrderMethod = mMemorizeOrderMethod;
		mMemorizeOrderMethod = Integer.parseInt(preferences.getString(Constants.JV_SPN_MEMORIZE_ORDER_METHOD, "0"));

		return (mMemorizeOrderMethod != prevMemorizeOrderMethod);
	}

	public synchronized void loadData(SharedPreferences preferences, boolean launchApp) {
		// 암기 대상 단어들만을 필터링한다.
		mVocabularyListData.clear();
		mCurrentPosition = -1;
		mVocabularyListMemorizeSequence.clear();
		mMemorizeCompletedCount = JapanVocabularyManager.getInstance().getMemorizeTargetJvList(mVocabularyListData);

		assert mMemorizeCompletedCount >= 0;
		assert mMemorizeCompletedCount <= mVocabularyListData.size();
	
		switch (mMemorizeOrderMethod) {
		case 1:
			Collections.sort(mVocabularyListData, JapanVocabularyComparator.mVocabularyComparator);
			break;
		case 3:
			Collections.sort(mVocabularyListData, JapanVocabularyComparator.mVocabularyGanaComparator);
			break;
		case 2:
			Collections.sort(mVocabularyListData, JapanVocabularyComparator.mVocabularyTranslationComparator);
			break;
		default:
			break;
		}
		
		if (launchApp == true) {
			loadVocabularyPosition(preferences);
		}
	}

	public synchronized boolean isValidVocabularyPosition() {
        return mCurrentPosition >= 0 && mCurrentPosition < mVocabularyListData.size();
    }
	
	public synchronized int getCount() {
		return mVocabularyListData.size();
	}

	public synchronized void setMemorizeCompletedAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			JapanVocabulary jpVocabulary = mVocabularyListData.get(mCurrentPosition);
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
			return mVocabularyListData.get(mCurrentPosition).getIdx();
		} else {
			assert false;
		}
		
		return -1;
	}

	private void loadVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		int latestMemorizeOrderMethod = preferences.getInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, 0/* 랜덤 */);
		if (latestMemorizeOrderMethod == mMemorizeOrderMethod && mMemorizeOrderMethod != 0/* 랜덤 */) {
			int prevCurrentPosition = mCurrentPosition;
			mCurrentPosition = preferences.getInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);
			
			if (isValidVocabularyPosition() == false) {
				mCurrentPosition = prevCurrentPosition;
			}
		}
	}

	public void saveVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		Editor edit = preferences.edit();
		edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, mMemorizeOrderMethod);
		if (mMemorizeOrderMethod == 0/* 랜덤 */) {
			edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);
		} else {
			if (mCurrentPosition != -1) {
				edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentPosition - 1);
			} else {
				edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentPosition);
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
			return mVocabularyListData.get(mCurrentPosition);
		}

		return null;
	}
	
	public synchronized JapanVocabulary movePosition(int position) {
		int prevCurrentPosition = mCurrentPosition;

		mCurrentPosition = position;
		if (isValidVocabularyPosition() == true) {
			return mVocabularyListData.get(mCurrentPosition);
		} else {
			assert false;
			mCurrentPosition = prevCurrentPosition;
		}

		return null;
	}
	
	@Override
	public synchronized JapanVocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		Integer value = mVocabularyListMemorizeSequence.pop();
		if (value != null) {
			int prevCurrentPosition = mCurrentPosition;

			mCurrentPosition = value;
			if (isValidVocabularyPosition() == true) {
				return mVocabularyListData.get(mCurrentPosition);
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
		
		if (mVocabularyListData.isEmpty() == true || mMemorizeCompletedCount >= mVocabularyListData.size()) {
			if (mMemorizeOrderMethod == 0/* 랜덤 */) { 
				mCurrentPosition = -1;
			} else {
				mCurrentPosition = mVocabularyListData.size() - 1;
			}

			sbErrMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (isValidVocabularyPosition() == true) {
				Integer value = mVocabularyListMemorizeSequence.popNoRemove();
				if (value != null) {
					if (mCurrentPosition != value) {
						mVocabularyListMemorizeSequence.push(mCurrentPosition);
					}
				} else {
					mVocabularyListMemorizeSequence.push(mCurrentPosition);
				}
			}

			if (mMemorizeOrderMethod == 0/* 랜덤 */) {
				int prevCurrentPosition = mCurrentPosition;
				int memorizeUncompletedCount = mVocabularyListData.size() - mMemorizeCompletedCount;
				
				do {
					int uncompletedCount = 0;
					int targetUncompletedCount = mRandom.nextInt(memorizeUncompletedCount) + 1;

					for (int index = 0; index < mVocabularyListData.size(); ++index) {
						if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
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
				for (int index = mCurrentPosition + 1; index < mVocabularyListData.size(); ++index) {
					if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
						mCurrentPosition = index;
						bFindSucceeded = true;
						break;
					}
				}
				
				if (bFindSucceeded == false) {
					for (int index = 0; index < mCurrentPosition; ++index) {
						if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
							mCurrentPosition = index;
							bFindSucceeded = true;
							break;
						}
					}
				}

				assert bFindSucceeded == true;
			}

			if (isValidVocabularyPosition() == true) {
				return mVocabularyListData.get(mCurrentPosition);
			}
		}

		return null;
	}
	
	public MemorizeTarget getMemorizeTargetItem() {
		return mMemorizeTarget;
	}

	public StringBuilder getMemorizeVocabularyInfo() {
		assert isValidVocabularyPosition() == true;

		StringBuilder sb = new StringBuilder();
		sb.append("암기완료 ").append(mMemorizeCompletedCount).append("개 / 전체 ").append(mVocabularyListData.size()).append("개");
		return sb;
	}

}
