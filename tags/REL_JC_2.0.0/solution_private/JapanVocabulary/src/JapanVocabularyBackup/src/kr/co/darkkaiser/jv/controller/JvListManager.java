package kr.co.darkkaiser.jv.controller;

import kr.co.darkkaiser.jv.data.JapanVocabulary;

//@@@@@
public class JvListManager {
	
	private JvList mJvList = null;

	public JvListManager() {
	}

	public synchronized void setVocabularySeekList(JvList jvList) {
		mJvList = jvList;
	}

	public synchronized boolean isValid() {
		if (mJvList == null)
			return false;

		return true;
	}

	public synchronized JapanVocabulary nextVocabulary(StringBuilder sbErrMessage) {
		if (isValid() == false)
			return null;

		return mJvList.nextVocabulary(sbErrMessage);
	}

	public synchronized JapanVocabulary previousVocabulary(StringBuilder sbErrMessage) {
		if (isValid() == false)
			return null;

		return mJvList.previousVocabulary(sbErrMessage);
	}

	public synchronized JapanVocabulary getCurrentVocabulary() {
		if (isValid() == false)
			return null;

		return mJvList.getCurrentVocabulary();
	}

}
