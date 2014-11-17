package kr.co.darkkaiser.jv.vocabulary.list.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.util.CircularBuffer;
import kr.co.darkkaiser.jv.vocabulary.MemorizeTarget;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyComparator;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;

public class MemorizeTargetVocabularyList implements IVocabularyList {

	private Random mRandom = new Random();

	// 암기대상 단어 리스트
	private ArrayList<Vocabulary> mVocabularyListData = new ArrayList<Vocabulary>();

	// 암기대상 단어의 암기순서
	private CircularBuffer<Integer> mVocabularyListMemorizeOrder = new CircularBuffer<Integer>();

	// 암기대상 단어 리스트 중에서 암기 완료한 단어의 갯수
	private int mMemorizeCompletedCount = 0;

	// 현재 화면에 보여지고 있는 암기대상 단어 위치
	private int mCurrentVocabularyPosition = -1;

	// (환경설정 값)단어 암기 순서(무작위, 순차(...))
	private int mMemorizeOrderMethod = 0/* 랜덤 */;

    // (환경설정 값)화면에 출력 할 암기 대상 단어의 항목(한자, 히라가나/가타가나)
	private MemorizeTarget mMemorizeTarget = MemorizeTarget.VOCABULARY;

	public MemorizeTargetVocabularyList() {
		
	}

    // @@@@@
	public boolean reloadPreference(Context context, SharedPreferences preferences) {
        assert context != null;
        assert preferences != null;

        String memorizeTargetDafultValue = Integer.toString(context.getResources().getInteger(R.integer.memorize_target_default_value));

        // 화면에 출력 할 암기 대상 단어의 항목을 읽는다.
        String memorizeTarget = preferences.getString(context.getString(R.string.as_memorize_target_key), memorizeTargetDafultValue);
        if (TextUtils.equals(memorizeTarget, memorizeTargetDafultValue) == true)
			mMemorizeTarget = MemorizeTarget.VOCABULARY;
		else
			mMemorizeTarget = MemorizeTarget.VOCABULARY_GANA;

		// 단어 암기 순서를 읽는다.
		int prevMemorizeOrderMethod = mMemorizeOrderMethod;
		mMemorizeOrderMethod = Integer.parseInt(preferences.getString(context.getString(R.string.as_memorize_order_method_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_order_method_default_value))));

        // 암기대상 단어를 재로드해야하는가?@@@@@
		return (mMemorizeOrderMethod != prevMemorizeOrderMethod);
	}

    // @@@@@
    public synchronized void loadData(SharedPreferences preferences, boolean executeFromApp) {
		// 암기 대상 단어들만을 필터링한다.
		mVocabularyListData.clear();
		mCurrentVocabularyPosition = -1;
		mVocabularyListMemorizeOrder.clear();
		mMemorizeCompletedCount = VocabularyManager.getInstance().getMemorizeTargetJvList(mVocabularyListData);

		assert mMemorizeCompletedCount >= 0;
		assert mMemorizeCompletedCount <= mVocabularyListData.size();
	
		switch (mMemorizeOrderMethod) {
		case 1:
			Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyComparator);
			break;
		case 3:
			Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyGanaComparator);
			break;
		case 2:
			Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyTranslationComparator);
			break;
		default:
			break;
		}

		if (executeFromApp == true) loadVocabularyPosition(preferences);
	}

    // @@@@@
    public synchronized void setMemorizeCompletedAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			Vocabulary vocabulary = mVocabularyListData.get(mCurrentVocabularyPosition);
			if (vocabulary != null && vocabulary.isMemorizeCompleted() == false) {
				++mMemorizeCompletedCount;
				vocabulary.setMemorizeCompleted(true, true, true);
			}
		} else {
			assert false;
		}
	}

    // @@@@@
    public synchronized long getIdxAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			return mVocabularyListData.get(mCurrentVocabularyPosition).getIdx();
		} else {
			assert false;
		}

		return -1;
	}

    // @@@@@
    private void loadVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		int latestMemorizeOrderMethod = preferences.getInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, 0/* 랜덤 */);
		if (latestMemorizeOrderMethod == mMemorizeOrderMethod && mMemorizeOrderMethod != 0/* 랜덤 */) {
			int prevCurrentPosition = mCurrentVocabularyPosition;
			mCurrentVocabularyPosition = preferences.getInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);

			if (isValidVocabularyPosition() == false) {
				mCurrentVocabularyPosition = prevCurrentPosition;
			}
		}
	}

    // @@@@@
    public void saveVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		Editor edit = preferences.edit();
		edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, mMemorizeOrderMethod);
		if (mMemorizeOrderMethod == 0/* 랜덤 */) {
			edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);
		} else {
			if (mCurrentVocabularyPosition != -1) {
				edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentVocabularyPosition - 1);
			} else {
				edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentVocabularyPosition);
			}
		}

		edit.commit();
	}

    public synchronized boolean isValidVocabularyPosition() {
        return mCurrentVocabularyPosition >= 0 && mCurrentVocabularyPosition < mVocabularyListData.size();
    }

    public synchronized int getCurrentVocabularyPosition() {
		return mCurrentVocabularyPosition;
	}

	public synchronized Vocabulary moveVocabularyPosition(int position) {
		int prevCurrentPosition = mCurrentVocabularyPosition;

		mCurrentVocabularyPosition = position;
		if (isValidVocabularyPosition() == true) {
			return mVocabularyListData.get(mCurrentVocabularyPosition);
		} else {
			assert false;
			mCurrentVocabularyPosition = prevCurrentPosition;
		}

		return null;
	}

    @Override
    public synchronized Vocabulary getCurrentVocabulary() {
        if (isValidVocabularyPosition() == true) return mVocabularyListData.get(mCurrentVocabularyPosition);
        return null;
    }

	@Override
    public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		Integer value = mVocabularyListMemorizeOrder.pop();
		if (value != null) {
			int prevCurrentPosition = mCurrentVocabularyPosition;

			mCurrentVocabularyPosition = value;
			if (isValidVocabularyPosition() == true) {
				return mVocabularyListData.get(mCurrentVocabularyPosition);
			} else {
				assert false;
				mCurrentVocabularyPosition = prevCurrentPosition;
			}
		} else {
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
    // @@@@@
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrMessage) {
		assert sbErrMessage != null;

		if (mVocabularyListData.isEmpty() == true || mMemorizeCompletedCount >= mVocabularyListData.size()) {
			if (mMemorizeOrderMethod == 0/* 랜덤 */) mCurrentVocabularyPosition = -1;
            else mCurrentVocabularyPosition = mVocabularyListData.size() - 1;

			sbErrMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (isValidVocabularyPosition() == true) {
				Integer value = mVocabularyListMemorizeOrder.popNoRemove();
				if (value != null) {
					if (mCurrentVocabularyPosition != value)
                        mVocabularyListMemorizeOrder.push(mCurrentVocabularyPosition);
				} else {
					mVocabularyListMemorizeOrder.push(mCurrentVocabularyPosition);
				}
			}

			if (mMemorizeOrderMethod == 0/* 랜덤 */) {
				int prevCurrentPosition = mCurrentVocabularyPosition;
				int memorizeUncompletedCount = mVocabularyListData.size() - mMemorizeCompletedCount;
				
				do {
					int uncompletedCount = 0;
					int targetUncompletedCount = mRandom.nextInt(memorizeUncompletedCount) + 1;

					for (int index = 0; index < mVocabularyListData.size(); ++index) {
						if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
							++uncompletedCount;
							
							if (uncompletedCount == targetUncompletedCount) {
								mCurrentVocabularyPosition = index;
								break;
							}
						}
					}
				} while (memorizeUncompletedCount > 1 && prevCurrentPosition == mCurrentVocabularyPosition);
			} else {
				boolean bFindSucceeded = false;
				for (int index = mCurrentVocabularyPosition + 1; index < mVocabularyListData.size(); ++index) {
					if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
						mCurrentVocabularyPosition = index;
						bFindSucceeded = true;
						break;
					}
				}
				
				if (bFindSucceeded == false) {
					for (int index = 0; index < mCurrentVocabularyPosition; ++index) {
						if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
							mCurrentVocabularyPosition = index;
							bFindSucceeded = true;
							break;
						}
					}
				}

				assert bFindSucceeded == true;
			}

			if (isValidVocabularyPosition() == true) return mVocabularyListData.get(mCurrentVocabularyPosition);
		}

		return null;
	}

    public MemorizeTarget getMemorizeTarget() {
        return mMemorizeTarget;
    }

    public synchronized int getVocabularyCount() {
        return mVocabularyListData.size();
    }

    // @@@@@
    public StringBuilder getMemorizeVocabularyInfo() {
		assert isValidVocabularyPosition() == true;

		StringBuilder sb = new StringBuilder();
		sb.append("암기완료 ").append(mMemorizeCompletedCount).append("개 / 전체 ").append(mVocabularyListData.size()).append("개");
		return sb;
	}

}
