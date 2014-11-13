package kr.co.darkkaiser.jv.vocabulary.list;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

public class VocabularyListWrapper {
	
	private IVocabularyList mVocabularyList = null;

	public VocabularyListWrapper() {

	}

	public synchronized void setVocabularySeekList(IVocabularyList vocabularyList) {
		mVocabularyList = vocabularyList;
	}

	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrMessage) {
		if (isValid() == false)
			return null;

		return mVocabularyList.nextVocabulary(sbErrMessage);
	}

	public synchronized Vocabulary previousVocabulary(StringBuilder sbErrMessage) {
		if (isValid() == false)
			return null;

		return mVocabularyList.previousVocabulary(sbErrMessage);
	}

	public synchronized Vocabulary getCurrentVocabulary() {
		if (isValid() == false)
			return null;

		return mVocabularyList.getCurrentVocabulary();
	}

    public synchronized boolean isValid() {
        return mVocabularyList != null;
    }

}
