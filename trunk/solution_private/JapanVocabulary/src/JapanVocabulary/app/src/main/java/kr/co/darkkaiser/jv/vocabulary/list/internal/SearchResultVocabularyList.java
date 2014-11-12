package kr.co.darkkaiser.jv.vocabulary.list.internal;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;

public class SearchResultVocabularyList implements IVocabularyList {

	private int mCurrentPosition = -1;
	private ArrayList<Vocabulary> mVocabularyListData = null;

	public SearchResultVocabularyList(ArrayList<Vocabulary> vocabularyListData, int position) {
		assert position != -1;
		assert vocabularyListData != null;

        mCurrentPosition = position;
        mVocabularyListData = vocabularyListData;
	}

	@Override
	public synchronized Vocabulary getCurrentVocabulary() {
		if (isValid() == true) {
			return mVocabularyListData.get(mCurrentPosition);
		}

		return null;
	}

	@Override
	public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		int prevCurrentPosition = mCurrentPosition;

		--mCurrentPosition;
		if (isValid() == true) {
			return mVocabularyListData.get(mCurrentPosition);
		} else {
			mCurrentPosition = prevCurrentPosition;
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrMessage) {
		int nextCurrentPosition = mCurrentPosition;

		++mCurrentPosition;
		if (isValid() == true) {
			return mVocabularyListData.get(mCurrentPosition);
		} else {
			mCurrentPosition = nextCurrentPosition;
			sbErrMessage.append("다음 단어가 없습니다.");
		}

		return null;
	}

	private synchronized boolean isValid() {
        return mVocabularyListData != null && !(mCurrentPosition < 0 || mCurrentPosition >= mVocabularyListData.size());
    }
	
}
