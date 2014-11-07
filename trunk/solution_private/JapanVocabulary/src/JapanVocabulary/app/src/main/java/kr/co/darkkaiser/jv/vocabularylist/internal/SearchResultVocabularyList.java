package kr.co.darkkaiser.jv.vocabularylist.internal;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.vocabularylist.IVocabularyList;
import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabulary;

public class SearchResultVocabularyList implements IVocabularyList {

	private int mCurrentPosition = -1;
	private ArrayList<JapanVocabulary> mVocabularyListData = null;

	public SearchResultVocabularyList(ArrayList<JapanVocabulary> vocabularyListData, int position) {
		assert position != -1;
		assert vocabularyListData != null;

        mCurrentPosition = position;
        mVocabularyListData = vocabularyListData;
	}

	@Override
	public synchronized JapanVocabulary getCurrentVocabulary() {
		if (isValid() == true) {
			return mVocabularyListData.get(mCurrentPosition);
		}

		return null;
	}

	@Override
	public synchronized JapanVocabulary previousVocabulary(StringBuilder sbErrorMessage) {
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
	public synchronized JapanVocabulary nextVocabulary(StringBuilder sbErrMessage) {
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
