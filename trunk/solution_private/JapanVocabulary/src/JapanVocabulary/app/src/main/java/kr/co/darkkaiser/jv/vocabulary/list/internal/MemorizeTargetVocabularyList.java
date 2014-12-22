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
import kr.co.darkkaiser.jv.vocabulary.MemorizeOrder;
import kr.co.darkkaiser.jv.vocabulary.MemorizeTarget;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyComparator;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.VocabularyList;
import kr.co.darkkaiser.jv.vocabulary.list.VocabularyListSeek;

public class MemorizeTargetVocabularyList implements VocabularyList, VocabularyListSeek {

	private Random mRandom = new Random();

	// 암기대상 단어 리스트
	private ArrayList<Vocabulary> mVocabularyListData = new ArrayList<Vocabulary>();

	// 이전부터 현재까지의 암기단어 순서
	private CircularBuffer<Integer> mVocabularyListMemorizeOrder = new CircularBuffer<Integer>();

	// 현재 화면에 보여지고 있는 암기단어의 위치
	private int mPosition = -1;

    // 암기를 완료한 단어의 갯수
    private int mMemorizeCompletedCount = 0;

	// 암기대상 단어 암기순서(무작위, 순차(...))
	private MemorizeOrder mMemorizeOrder = MemorizeOrder.RANDOM;

    // 암기대상 문자(한자, 히라가나/가타가나)
	private MemorizeTarget mMemorizeTarget = MemorizeTarget.VOCABULARY;

	public MemorizeTargetVocabularyList() {
		clearVocabularyData();
	}

	public void resetMemorizeSettings(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

        // 화면에 출력 할 암기대상 단어의 항목을 읽는다.
        String memorizeTarget = sharedPreferences.getString(context.getString(R.string.as_memorize_target_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_target_default_value)));
        if (TextUtils.equals(memorizeTarget, Integer.toString(MemorizeTarget.VOCABULARY_GANA.ordinal())) == true)
            mMemorizeTarget = MemorizeTarget.VOCABULARY_GANA;
        else
            mMemorizeTarget = MemorizeTarget.VOCABULARY;

		// 단어 암기순서를 읽는다.
		MemorizeOrder prevMemorizeOrder = mMemorizeOrder;
		int memorizeOrder = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.as_memorize_order_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_order_default_value))));
        if (memorizeOrder == MemorizeOrder.VOCABULARY.ordinal())
            mMemorizeOrder = MemorizeOrder.VOCABULARY;
        else if (memorizeOrder == MemorizeOrder.VOCABULARY_TRANSLATION.ordinal())
            mMemorizeOrder = MemorizeOrder.VOCABULARY_TRANSLATION;
        else if (memorizeOrder == MemorizeOrder.VOCABULARY_GANA.ordinal())
            mMemorizeOrder = MemorizeOrder.VOCABULARY_GANA;
        else
            mMemorizeOrder = MemorizeOrder.RANDOM;

        // 단어 암기순서가 변경되어 암기대상 단어를 재로드해야 하는 경우라면 이전에 읽어들인 암기대상 단어를 모두 제거한다.
        if (mMemorizeOrder != prevMemorizeOrder)
            clearVocabularyData();
	}

    public synchronized void loadVocabularyData(SharedPreferences sharedPreferences, boolean firstLoadVocabularyData) {
        assert sharedPreferences != null;

        clearVocabularyData();

        // 암기대상 단어를 읽어들인다.
        mMemorizeCompletedCount = VocabularyManager.getInstance().getMemorizeTargetVocabularyList(mVocabularyListData);

        assert mMemorizeCompletedCount >= 0;
        assert mMemorizeCompletedCount <= mVocabularyListData.size();

        // 읽어들인 암기대상 단어를 암기순서대로 정렬한다.
        if (mMemorizeOrder == MemorizeOrder.VOCABULARY)
            Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyComparator);
        else if (mMemorizeOrder == MemorizeOrder.VOCABULARY_TRANSLATION)
            Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyTranslationComparator);
        else if (mMemorizeOrder == MemorizeOrder.VOCABULARY_GANA)
            Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyGanaComparator);
        else
            assert false;

        if (firstLoadVocabularyData == true) {
            // 암기순서가 랜덤순이 아닐경우 마지막에 암기한 단어의 위치를 읽어들인다.
            int latestMemorizeOrder = sharedPreferences.getInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_ORDER, MemorizeOrder.RANDOM.ordinal());
            if (latestMemorizeOrder == mMemorizeOrder.ordinal() && mMemorizeOrder != MemorizeOrder.RANDOM) {
                mPosition = sharedPreferences.getInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION, -1);

                if (isValidPosition() == false)
                    mPosition = -1;
            }
        }
    }

    private synchronized void clearVocabularyData() {
        mPosition = -1;
        mMemorizeCompletedCount = 0;

        mVocabularyListData.clear();
        mVocabularyListMemorizeOrder.clear();
    }

    @Override
    public synchronized Vocabulary getVocabulary() {
        if (isValidPosition() == true)
            return mVocabularyListData.get(mPosition);

        return null;
    }

    public synchronized long getVocabularyIdx() {
        Vocabulary vocabulary = getVocabulary();
        if (vocabulary != null)
            return vocabulary.getIdx();

        return -1;
    }

	@Override
    public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
        assert sbErrorMessage != null;

        Integer value = mVocabularyListMemorizeOrder.pop();
		if (value != null) {
			int prevPosition = mPosition;

			mPosition = value;
			if (isValidPosition() == true) {
				return mVocabularyListData.get(mPosition);
			} else {
				assert false;
				mPosition = prevPosition;
			}
		} else {
			sbErrorMessage.append("이전 암기 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrorMessage) {
		assert sbErrorMessage != null;

        savePositionInMemorizeOrder();

        if (mVocabularyListData.isEmpty() == true || mMemorizeCompletedCount >= mVocabularyListData.size()) {
            mPosition = -1;
			sbErrorMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (mMemorizeOrder == MemorizeOrder.RANDOM) {
				int prevPosition = mPosition;
				int totalMemorizeUncompletedCount = mVocabularyListData.size() - mMemorizeCompletedCount;

				do {
					int memorizeUncompletedCount = 0;
					int targetMemorizeUncompletedCount = mRandom.nextInt(totalMemorizeUncompletedCount) + 1;

					for (int index = 0; index < mVocabularyListData.size(); ++index) {
						if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
							++memorizeUncompletedCount;
							
							if (memorizeUncompletedCount == targetMemorizeUncompletedCount) {
								mPosition = index;
								break;
							}
						}
					}
				} while (totalMemorizeUncompletedCount > 1 && prevPosition == mPosition);
			} else {
				boolean isFindSucceeded = false;
				for (int index = mPosition + 1; index < mVocabularyListData.size(); ++index) {
					if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
						mPosition = index;
						isFindSucceeded = true;
						break;
					}
				}
				
				if (isFindSucceeded == false) {
					for (int index = 0; index < mPosition; ++index) {
						if (mVocabularyListData.get(index).isMemorizeCompleted() == false) {
							mPosition = index;
							isFindSucceeded = true;
							break;
						}
					}
				}

				assert isFindSucceeded == true;
			}

			if (isValidPosition() == true)
                return mVocabularyListData.get(mPosition);
		}

		return null;
	}

    @Override
    public synchronized void setMemorizeTarget(boolean flag) {
        if (isValidPosition() == true) {
            Vocabulary vocabulary = mVocabularyListData.get(mPosition);
            if (vocabulary != null) {
                if (flag == true && vocabulary.isMemorizeTarget() == false) {
                    vocabulary.setMemorizeTarget(true);

                    // 사용자 암기정보를 갱신합니다.
                    VocabularyManager.getInstance().updateUserVocabulary(vocabulary);
                } else if (flag == false && vocabulary.isMemorizeTarget() == true) {
                    vocabulary.setMemorizeTarget(false);

                    // 사용자 암기정보를 갱신합니다.
                    VocabularyManager.getInstance().updateUserVocabulary(vocabulary);
                }

                // todo @@@@@ 암기대상단어가 비대상으로, 비대상 단어가 대상단어로 바뀌고 나서 정규화를 거쳐야 하는게 아닌지, 정규화했을때 메인에서 이상없는지 확인할 것
            }
        } else {
            assert false;
        }
    }

    @Override
    public synchronized void setMemorizeCompleted(boolean flag) {
        if (isValidPosition() == true) {
            Vocabulary vocabulary = mVocabularyListData.get(mPosition);
            if (vocabulary != null) {
                if (flag == true && vocabulary.isMemorizeCompleted() == false) {
                    ++mMemorizeCompletedCount;
                    vocabulary.setMemorizeCompleted(true, true);

                    // 사용자 암기정보를 갱신합니다.
                    VocabularyManager.getInstance().updateUserVocabulary(vocabulary);
                } else if (flag == false && vocabulary.isMemorizeCompleted() == true) {
                    --mMemorizeCompletedCount;
                    vocabulary.setMemorizeCompleted(false, false);

                    // 사용자 암기정보를 갱신합니다.
                    VocabularyManager.getInstance().updateUserVocabulary(vocabulary);
                }
            }
        } else {
            assert false;
        }
    }

    @Override
    public synchronized int getCount() {
        return mVocabularyListData.size();
    }

    @Override
    public boolean canSeek() {
        return !(getCount() == 0 || mMemorizeOrder == MemorizeOrder.RANDOM);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public synchronized boolean isValidPosition() {
        return mPosition >= 0 && mPosition < mVocabularyListData.size();
    }

    @Override
    public synchronized int getPosition() {
        return mPosition;
    }

    public synchronized Vocabulary movePosition(int position) {
        int prevPosition = mPosition;

        mPosition = position;
        if (isValidPosition() == true) {
            return mVocabularyListData.get(mPosition);
        } else {
            assert false;
            mPosition = prevPosition;
        }

        return null;
    }

    public synchronized void savePositionInMemorizeOrder() {
        // '다음' 버튼을 눌렀을 때 이전 단어로 돌아가기 위해 현재 보여지고 있는 암기단어의 위치를 저장한다.
        if (isValidPosition() == true) {
            Integer value = mVocabularyListMemorizeOrder.popNoRemove();
            if (value != null) {
                // 이전 단어와 현재 단어가 동일한 위치(동일한 단어)라면 중복 추가되지 않도록 한다.
                if (mPosition != value)
                    mVocabularyListMemorizeOrder.push(mPosition);
            } else {
                mVocabularyListMemorizeOrder.push(mPosition);
            }
        }
    }

    public synchronized void savePositionInSharedPreferences(SharedPreferences sharedPreferences) {
        assert sharedPreferences != null;

        Editor edit = sharedPreferences.edit();
        edit.putInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_ORDER, mMemorizeOrder.ordinal());
        if (mMemorizeOrder == MemorizeOrder.RANDOM)
            edit.putInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION, -1);
        else
            edit.putInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION, mPosition);

        edit.commit();
    }

    public synchronized MemorizeOrder getMemorizeOrder() { return mMemorizeOrder; }

    public synchronized MemorizeTarget getMemorizeTarget() {
        return mMemorizeTarget;
    }

    public synchronized String getMemorizeVocabularyInfo() {
        assert isValidPosition() == true;
        return "암기완료 " + mMemorizeCompletedCount + "개 / 암기대상 " + mVocabularyListData.size() + "개";
    }

}
