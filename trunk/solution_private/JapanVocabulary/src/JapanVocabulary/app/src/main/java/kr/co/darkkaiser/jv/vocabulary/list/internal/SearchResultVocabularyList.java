package kr.co.darkkaiser.jv.vocabulary.list.internal;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;

public class SearchResultVocabularyList implements IVocabularyList {

	private int mPosition = -1;

	private ArrayList<Vocabulary> mVocabularyList = null;

	public SearchResultVocabularyList(ArrayList<Vocabulary> vocabularyList, int position) {
		assert position != -1;
		assert vocabularyList != null;

        mPosition = position;
        mVocabularyList = vocabularyList;
	}

	@Override
	public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		int olcPosition = mPosition;

		--mPosition;
		if (isValid() == true) {
			return mVocabularyList.get(mPosition);
		} else {
			mPosition = olcPosition;
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrMessage) {
		int oldPosition = mPosition;

		++mPosition;
		if (isValid() == true) {
			return mVocabularyList.get(mPosition);
		} else {
			mPosition = oldPosition;
			sbErrMessage.append("다음 단어가 없습니다.");
		}

		return null;
	}

    @Override
    public synchronized Vocabulary getVocabulary() {
        if (isValid() == true)
            return mVocabularyList.get(mPosition);

        return null;
    }

    @Override
	public synchronized boolean isValid() {
        return mVocabularyList != null && !(mPosition < 0 || mPosition >= mVocabularyList.size());
    }
	
}
