package kr.co.darkkaiser.jv.vocabulary.list.internal;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;

public class SearchResultVocabularyList implements IVocabularyList {

	private int mCurrentPosition = -1;

	private ArrayList<Vocabulary> mVocabularyList = null;

	public SearchResultVocabularyList(ArrayList<Vocabulary> vocabularyList, int position) {
		assert position != -1;
		assert vocabularyList != null;

        mCurrentPosition = position;
        mVocabularyList = vocabularyList;
	}

	@Override
	public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		int olcCurrentPosition = mCurrentPosition;

		--mCurrentPosition;
		if (isValid() == true) {
			return mVocabularyList.get(mCurrentPosition);
		} else {
			mCurrentPosition = olcCurrentPosition;
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrMessage) {
		int oldCurrentPosition = mCurrentPosition;

		++mCurrentPosition;
		if (isValid() == true) {
			return mVocabularyList.get(mCurrentPosition);
		} else {
			mCurrentPosition = oldCurrentPosition;
			sbErrMessage.append("다음 단어가 없습니다.");
		}

		return null;
	}

    @Override
    public synchronized Vocabulary getCurrentVocabulary() {
        if (isValid() == true)
            return mVocabularyList.get(mCurrentPosition);

        return null;
    }

    @Override
	public synchronized boolean isValid() {
        return mVocabularyList != null && !(mCurrentPosition < 0 || mCurrentPosition >= mVocabularyList.size());
    }
	
}
