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

	private Random random = new Random();

	// 암기대상 단어 리스트
	private ArrayList<Vocabulary> vocabularyListData = new ArrayList<Vocabulary>();

	// 이전부터 현재까지의 암기단어 순서
	private CircularBuffer<Integer> vocabularyListMemorizeOrder = new CircularBuffer<Integer>();

	// 현재 화면에 보여지고 있는 암기단어의 위치
	private int position = -1;

    // 암기를 완료한 단어의 갯수
    private int memorizeCompletedCount = 0;

	// 암기대상 단어 암기순서(무작위, 순차(...))
	private MemorizeOrder memorizeOrder = MemorizeOrder.RANDOM;

    // 암기대상 문자(한자, 히라가나/가타가나)
	private MemorizeTarget memorizeTarget = MemorizeTarget.VOCABULARY;

	public MemorizeTargetVocabularyList() {
		clearVocabularyData();
	}

	public void resetMemorizeSettings(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

        // 화면에 출력 할 암기대상 단어의 항목을 읽는다.
        String memorizeTarget = sharedPreferences.getString(context.getString(R.string.as_vocabulary_memorize_target_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_target_default_value)));
        if (TextUtils.equals(memorizeTarget, Integer.toString(MemorizeTarget.VOCABULARY_GANA.ordinal())) == true)
            this.memorizeTarget = MemorizeTarget.VOCABULARY_GANA;
        else
            this.memorizeTarget = MemorizeTarget.VOCABULARY;

		// 단어 암기순서를 읽는다.
		MemorizeOrder prevMemorizeOrder = this.memorizeOrder;
		int memorizeOrder = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.as_vocabulary_memorize_order_key), Integer.toString(context.getResources().getInteger(R.integer.memorize_order_default_value))));
        if (memorizeOrder == MemorizeOrder.VOCABULARY.ordinal())
            this.memorizeOrder = MemorizeOrder.VOCABULARY;
        else if (memorizeOrder == MemorizeOrder.VOCABULARY_TRANSLATION.ordinal())
            this.memorizeOrder = MemorizeOrder.VOCABULARY_TRANSLATION;
        else if (memorizeOrder == MemorizeOrder.VOCABULARY_GANA.ordinal())
            this.memorizeOrder = MemorizeOrder.VOCABULARY_GANA;
        else
            this.memorizeOrder = MemorizeOrder.RANDOM;

        // 단어 암기순서가 변경되어 암기대상 단어를 재로드해야 하는 경우라면 이전에 읽어들인 암기대상 단어를 모두 제거한다.
        if (this.memorizeOrder != prevMemorizeOrder)
            clearVocabularyData();
	}

    public synchronized void loadVocabularyData(SharedPreferences sharedPreferences, boolean firstLoadVocabularyData) {
        assert sharedPreferences != null;

        clearVocabularyData();

        // 암기대상 단어를 읽어들인다.
        this.memorizeCompletedCount = VocabularyManager.getInstance().getMemorizeTargetVocabularyList(this.vocabularyListData);

        assert this.memorizeCompletedCount >= 0;
        assert this.memorizeCompletedCount <= this.vocabularyListData.size();

        // 읽어들인 암기대상 단어를 암기순서대로 정렬한다.
        if (this.memorizeOrder == MemorizeOrder.VOCABULARY)
            Collections.sort(this.vocabularyListData, VocabularyComparator.mVocabularyComparator);
        else if (this.memorizeOrder == MemorizeOrder.VOCABULARY_TRANSLATION)
            Collections.sort(this.vocabularyListData, VocabularyComparator.mVocabularyTranslationComparator);
        else if (this.memorizeOrder == MemorizeOrder.VOCABULARY_GANA)
            Collections.sort(this.vocabularyListData, VocabularyComparator.mVocabularyGanaComparator);
        else
            assert false;

        if (firstLoadVocabularyData == true) {
            // 암기순서가 랜덤순이 아닐경우 마지막에 암기한 단어의 위치를 읽어들인다.
            int latestMemorizeOrder = sharedPreferences.getInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_ORDER, MemorizeOrder.RANDOM.ordinal());
            if (latestMemorizeOrder == this.memorizeOrder.ordinal() && this.memorizeOrder != MemorizeOrder.RANDOM) {
                this.position = sharedPreferences.getInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION, -1);

                if (isValidPosition() == false)
                    this.position = -1;
            }
        }
    }

    private synchronized void clearVocabularyData() {
        this.position = -1;
        this.memorizeCompletedCount = 0;

        this.vocabularyListData.clear();
        this.vocabularyListMemorizeOrder.clear();
    }

    @Override
    public synchronized Vocabulary getVocabulary() {
        if (isValidPosition() == true)
            return this.vocabularyListData.get(this.position);

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

        Integer value = this.vocabularyListMemorizeOrder.pop();
		if (value != null) {
			int prevPosition = this.position;

            this.position = value;
			if (isValidPosition() == true) {
				return this.vocabularyListData.get(this.position);
			} else {
				assert false;
                this.position = prevPosition;
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

        if (this.vocabularyListData.isEmpty() == true || this.memorizeCompletedCount >= this.vocabularyListData.size()) {
            this.position = -1;
			sbErrorMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (this.memorizeOrder == MemorizeOrder.RANDOM) {
				int prevPosition = this.position;
				int totalMemorizeUncompletedCount = this.vocabularyListData.size() - this.memorizeCompletedCount;

				do {
					int memorizeUncompletedCount = 0;
					int targetMemorizeUncompletedCount = this.random.nextInt(totalMemorizeUncompletedCount) + 1;

					for (int index = 0; index < this.vocabularyListData.size(); ++index) {
						if (this.vocabularyListData.get(index).isMemorizeCompleted() == false) {
							++memorizeUncompletedCount;
							
							if (memorizeUncompletedCount == targetMemorizeUncompletedCount) {
                                this.position = index;
								break;
							}
						}
					}
				} while (totalMemorizeUncompletedCount > 1 && prevPosition == this.position);
			} else {
				boolean isFindSucceeded = false;
				for (int index = this.position + 1; index < this.vocabularyListData.size(); ++index) {
					if (this.vocabularyListData.get(index).isMemorizeCompleted() == false) {
                        this.position = index;
						isFindSucceeded = true;
						break;
					}
				}
				
				if (isFindSucceeded == false) {
					for (int index = 0; index < this.position; ++index) {
						if (this.vocabularyListData.get(index).isMemorizeCompleted() == false) {
                            this.position = index;
							isFindSucceeded = true;
							break;
						}
					}
				}

				assert isFindSucceeded == true;
			}

			if (isValidPosition() == true)
                return this.vocabularyListData.get(this.position);
		}

		return null;
	}

    @Override
    public synchronized void setMemorizeTarget(boolean flag) {
        if (isValidPosition() == true) {
            Vocabulary vocabulary = this.vocabularyListData.get(this.position);
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
            Vocabulary vocabulary = this.vocabularyListData.get(this.position);
            if (vocabulary != null) {
                if (flag == true && vocabulary.isMemorizeCompleted() == false) {
                    ++this.memorizeCompletedCount;
                    vocabulary.setMemorizeCompleted(true, true);

                    // 사용자 암기정보를 갱신합니다.
                    VocabularyManager.getInstance().updateUserVocabulary(vocabulary);
                } else if (flag == false && vocabulary.isMemorizeCompleted() == true) {
                    --this.memorizeCompletedCount;
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
        return this.vocabularyListData.size();
    }

    @Override
    public boolean canSeek() {
        return !(getCount() == 0 || this.memorizeOrder == MemorizeOrder.RANDOM);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public synchronized boolean isValidPosition() {
        return this.position >= 0 && this.position < this.vocabularyListData.size();
    }

    @Override
    public synchronized int getPosition() {
        return this.position;
    }

    public synchronized Vocabulary movePosition(int position) {
        int prevPosition = this.position;

        this.position = position;
        if (isValidPosition() == true) {
            return this.vocabularyListData.get(this.position);
        } else {
            assert false;
            this.position = prevPosition;
        }

        return null;
    }

    public synchronized void savePositionInMemorizeOrder() {
        // '다음' 버튼을 눌렀을 때 이전 단어로 돌아가기 위해 현재 보여지고 있는 암기단어의 위치를 저장한다.
        if (isValidPosition() == true) {
            Integer value = this.vocabularyListMemorizeOrder.popNoRemove();
            if (value != null) {
                // 이전 단어와 현재 단어가 동일한 위치(동일한 단어)라면 중복 추가되지 않도록 한다.
                if (this.position != value)
                    this.vocabularyListMemorizeOrder.push(this.position);
            } else {
                this.vocabularyListMemorizeOrder.push(this.position);
            }
        }
    }

    public synchronized void savePositionInSharedPreferences(SharedPreferences sharedPreferences) {
        assert sharedPreferences != null;

        Editor edit = sharedPreferences.edit();
        edit.putInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_ORDER, this.memorizeOrder.ordinal());
        if (this.memorizeOrder == MemorizeOrder.RANDOM)
            edit.putInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION, -1);
        else
            edit.putInt(Constants.SPKEY_LATEST_VOCABULARY_MEMORIZE_POSITION, this.position);

        edit.commit();
    }

    public synchronized MemorizeOrder getMemorizeOrder() { return this.memorizeOrder; }

    public synchronized MemorizeTarget getMemorizeTarget() {
        return this.memorizeTarget;
    }

    public synchronized String getMemorizeVocabularyInfo() {
        assert isValidPosition() == true;
        return "암기완료 " + this.memorizeCompletedCount + "개 / 암기대상 " + this.vocabularyListData.size() + "개";
    }

}
