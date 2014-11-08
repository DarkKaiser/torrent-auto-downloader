package kr.co.darkkaiser.jv.vocabulary.list;

import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabulary;

public class VocabularyListManager {
	
	private IVocabularyList mVocabularyList = null;

	public VocabularyListManager() {

	}

	public synchronized void setVocabularySeekList(IVocabularyList vocabularyList) {
		mVocabularyList = vocabularyList;
	}

	public synchronized JapanVocabulary nextVocabulary(StringBuilder sbErrMessage) {
		if (isValid() == false)
			return null;

		return mVocabularyList.nextVocabulary(sbErrMessage);
	}

	public synchronized JapanVocabulary previousVocabulary(StringBuilder sbErrMessage) {
		if (isValid() == false)
			return null;

		return mVocabularyList.previousVocabulary(sbErrMessage);
	}

	public synchronized JapanVocabulary getCurrentVocabulary() {
		if (isValid() == false)
			return null;

		return mVocabularyList.getCurrentVocabulary();
	}

    public synchronized boolean isValid() {
        if (mVocabularyList == null)
            return false;

        return true;
    }

}
