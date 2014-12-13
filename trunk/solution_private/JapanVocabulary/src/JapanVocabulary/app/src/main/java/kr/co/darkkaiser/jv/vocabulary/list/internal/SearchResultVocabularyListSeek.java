package kr.co.darkkaiser.jv.vocabulary.list.internal;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyListSeek;

public class SearchResultVocabularyListSeek implements IVocabularyListSeek {

	private int mPosition = -1;

    private SearchResultVocabularyList mSearchResultVocabularyList = null;

	public SearchResultVocabularyListSeek(SearchResultVocabularyList searchResultVocabularyList, int position) {
		assert searchResultVocabularyList != null;
        assert position >= 0 && position < mSearchResultVocabularyList.getCount();

        mPosition = position;
        mSearchResultVocabularyList = searchResultVocabularyList;
	}

    @Override
    public synchronized Vocabulary getVocabulary() {
        if (isValid() == true)
            return mSearchResultVocabularyList.getVocabulary(mPosition);

        return null;
    }

    @Override
	public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
        assert sbErrorMessage != null;

		int oldPosition = mPosition;

		--mPosition;
		if (isValid() == true) {
            return mSearchResultVocabularyList.getVocabulary(mPosition);
		} else {
			mPosition = oldPosition;
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrorMessage) {
        assert sbErrorMessage != null;

		int oldPosition = mPosition;

		++mPosition;
		if (isValid() == true) {
            return mSearchResultVocabularyList.getVocabulary(mPosition);
		} else {
			mPosition = oldPosition;
			sbErrorMessage.append("다음 단어가 없습니다.");
		}

		return null;
	}

    @Override
    public synchronized void setMemorizeTarget(boolean flag) {
        // @@@@@
        if (isValid() == true) {
            Vocabulary vocabulary = getVocabulary();
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
            }
        } else {
            assert false;
        }
    }

    @Override
    public synchronized void setMemorizeCompleted(boolean flag) {
        // @@@@@ mSearchResultVocabularyList 변수에 있는 함수를 이용하는 걸로 변경
        if (isValid() == true) {
            Vocabulary vocabulary = getVocabulary();
            if (vocabulary != null) {
                if (flag == true && vocabulary.isMemorizeCompleted() == false) {
                    vocabulary.setMemorizeCompleted(true, true);

                    // 사용자 암기정보를 갱신합니다.
                    VocabularyManager.getInstance().updateUserVocabulary(vocabulary);
                } else if (flag == false && vocabulary.isMemorizeCompleted() == true) {
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
	public synchronized boolean isValid() {
        return mSearchResultVocabularyList.isValidPosition(mPosition);
    }

    @Override
    public boolean canSeek() {
        return true;
    }

}
