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

	// 이전부터 현재까지의 암기단어 순서
	private CircularBuffer<Integer> mVocabularyListMemorizeOrder = new CircularBuffer<Integer>();

	// 현재 화면에 보여지고 있는 암기대상 단어의 위치
	private int mCurrentPosition = -1;

    // 암기대상 단어들 중에서 암기를 완료한 단어의 갯수
    private int mMemorizeCompletedCount = 0;

	// 단어 암기순서(무작위, 순차(...))
	private int mMemorizeOrderMethod = 0/* 랜덤 */;

    // 화면에 출력 할 암기대상 문자(한자, 히라가나/가타가나)
	private MemorizeTarget mMemorizeTarget = MemorizeTarget.VOCABULARY;

	public MemorizeTargetVocabularyList() {
		
	}

    // @@@@@
	public boolean reloadPreference(Context context, SharedPreferences preferences) {
        assert context != null;
        assert preferences != null;

        // 화면에 출력 할 암기 대상 단어의 항목을 읽는다.
        String memorizeTarget = preferences.getString(context.getString(R.string.as_memorize_target_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_target_default_value)));
        if (TextUtils.equals(memorizeTarget, Integer.toString(MemorizeTarget.VOCABULARY.ordinal())) == true)
            mMemorizeTarget = MemorizeTarget.VOCABULARY;
        else if (TextUtils.equals(memorizeTarget, Integer.toString(MemorizeTarget.VOCABULARY_GANA.ordinal())) == true)
            mMemorizeTarget = MemorizeTarget.VOCABULARY_GANA;
        else
            assert false;

		// 단어 암기 순서를 읽는다.
		int prevMemorizeOrderMethod = mMemorizeOrderMethod;
		mMemorizeOrderMethod = Integer.parseInt(preferences.getString(context.getString(R.string.as_memorize_order_method_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_order_method_default_value))));

        // 암기대상 단어를 재로드해야하는가?@@@@@
		return (mMemorizeOrderMethod != prevMemorizeOrderMethod);
	}

    // @@@@@
    public synchronized void setMemorizeCompletedAtVocabularyPosition() {
		if (isValid() == true) {
			Vocabulary vocabulary = mVocabularyListData.get(mCurrentPosition);
			if (vocabulary != null && vocabulary.isMemorizeCompleted() == false) {
				++mMemorizeCompletedCount;
				vocabulary.setMemorizeCompleted(true, true);
                VocabularyManager.getInstance().writeUserVocabularyInfo();
			}
		} else {
			assert false;
		}
	}

    // @@@@@
    public synchronized void loadVocabularyData(SharedPreferences preferences, boolean executeFromApp) {
        mCurrentPosition = -1;

        // 암기대상 단어를 읽어들인다.
        mVocabularyListData.clear();
        mVocabularyListMemorizeOrder.clear();
        mMemorizeCompletedCount = VocabularyManager.getInstance().getMemorizeTargetJvList(mVocabularyListData);

        assert mMemorizeCompletedCount >= 0;
        assert mMemorizeCompletedCount <= mVocabularyListData.size();

        switch (mMemorizeOrderMethod) {
            case 1/* 한자순 */:
                Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyComparator);
                break;
            case 2/* 뜻순 */:
                Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyTranslationComparator);
                break;
            case 3/* 히라가나/가타가나순 */:
                Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyGanaComparator);
                break;
            default:
                break;
        }

        if (executeFromApp == true) loadVocabularyPosition(preferences);
    }

    // @@@@@
    private void loadVocabularyPosition(SharedPreferences preferences) {
		assert preferences != null;

		int latestMemorizeOrderMethod = preferences.getInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_LATEST, 0/* 랜덤 */);
		if (latestMemorizeOrderMethod == mMemorizeOrderMethod && mMemorizeOrderMethod != 0/* 랜덤 */) {
			int prevCurrentPosition = mCurrentPosition;
			mCurrentPosition = preferences.getInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, -1);

			if (isValid() == false) {
				mCurrentPosition = prevCurrentPosition;
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
			if (mCurrentPosition != -1) {
				edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentPosition - 1);
			} else {
				edit.putInt(Constants.JV_SPN_MEMORIZE_ORDER_METHOD_INDEX_LATEST, mCurrentPosition);
			}
		}

		edit.commit();
	}

    @Override
    public synchronized boolean isValid() {
        return mCurrentPosition >= 0 && mCurrentPosition < mVocabularyListData.size();
    }

    public synchronized int getCurrentPosition() {
		return mCurrentPosition;
	}

	public synchronized Vocabulary movePosition(int position) {
		int prevCurrentPosition = mCurrentPosition;

		mCurrentPosition = position;
		if (isValid() == true) {
			return mVocabularyListData.get(mCurrentPosition);
		} else {
			assert false;
			mCurrentPosition = prevCurrentPosition;
		}

		return null;
	}

    @Override
    public synchronized Vocabulary getCurrentVocabulary() {
        if (isValid() == true)
            return mVocabularyListData.get(mCurrentPosition);

        return null;
    }

    public synchronized long getCurrentVocabularyIdx() {
        Vocabulary vocabulary = getCurrentVocabulary();
        if (vocabulary != null)
            return vocabulary.getIdx();

        return -1;
    }

	@Override
    public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		Integer value = mVocabularyListMemorizeOrder.pop();
		if (value != null) {
			int prevCurrentPosition = mCurrentPosition;

			mCurrentPosition = value;
			if (isValid() == true) {
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
    // @@@@@
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrMessage) {
		assert sbErrMessage != null;

		if (mVocabularyListData.isEmpty() == true || mMemorizeCompletedCount >= mVocabularyListData.size()) {
			if (mMemorizeOrderMethod == 0/* 랜덤 */) mCurrentPosition = -1;
            else mCurrentPosition = mVocabularyListData.size() - 1;

			sbErrMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (isValid() == true) {
				Integer value = mVocabularyListMemorizeOrder.popNoRemove();
				if (value != null) {
					if (mCurrentPosition != value)
                        mVocabularyListMemorizeOrder.push(mCurrentPosition);
				} else {
					mVocabularyListMemorizeOrder.push(mCurrentPosition);
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

			if (isValid() == true) return mVocabularyListData.get(mCurrentPosition);
		}

		return null;
	}

    public synchronized int getCount() {
        return mVocabularyListData.size();
    }

    public synchronized MemorizeTarget getMemorizeTarget() {
        return mMemorizeTarget;
    }

    public synchronized String getMemorizeVocabularyInfo() {
        assert isValid() == true;
        return "암기완료 " + mMemorizeCompletedCount + "개 / 전체 " + mVocabularyListData.size() + "개";
    }

}
