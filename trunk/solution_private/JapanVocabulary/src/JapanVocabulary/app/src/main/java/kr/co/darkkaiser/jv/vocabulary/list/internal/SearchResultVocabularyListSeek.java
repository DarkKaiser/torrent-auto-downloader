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
    }

    @Override
    public synchronized void setMemorizeCompleted(boolean flag) {
        // @@@@@
//        mVocabulary.setMemorizeCompleted(false, false);
//
//        // 사용자 암기정보를 갱신합니다. 아래 함수는 바깥으로
//        VocabularyManager.getInstance().updateUserVocabulary(mVocabulary);
    }

    @Override
    public synchronized Vocabulary getVocabulary() {
        if (isValid() == true)
            return mSearchResultVocabularyList.getVocabulary(mPosition);

        return null;
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
