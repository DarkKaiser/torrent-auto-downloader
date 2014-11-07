package kr.co.darkkaiser.jv.controller.internal;

import kr.co.darkkaiser.jv.controller.IVocabularyList;
import kr.co.darkkaiser.jv.data.JapanVocabulary;

public class MemorizeVocabularyListManager implements IVocabularyList {
	
	private IVocabularyList mVocabularyList = null;

	public MemorizeVocabularyListManager() {

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
