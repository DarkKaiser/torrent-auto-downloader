package kr.co.darkkaiser.jv.controller;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.data.JapanVocabulary;

//@@@@@
public class JvSearchList implements JvList {
	
	private int mCurrentPosition = -1;
	private ArrayList<JapanVocabulary> mJvListData = null;

	public JvSearchList(ArrayList<JapanVocabulary> jvListData, int position) {
		assert position != -1;
		assert jvListData != null;

		mJvListData = jvListData;
		mCurrentPosition = position;
	}

	@Override
	public JapanVocabulary getCurrentVocabulary() {
		if (isValid() == true) {
			return mJvListData.get(mCurrentPosition);
		}

		return null;
	}

	@Override
	public JapanVocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		int prevCurrentPosition = mCurrentPosition;

		--mCurrentPosition;
		if (isValid() == true) {
			return mJvListData.get(mCurrentPosition);
		} else {
			mCurrentPosition = prevCurrentPosition;
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public JapanVocabulary nextVocabulary(StringBuilder sbErrMessage) {
		int nextCurrentPosition = mCurrentPosition;

		++mCurrentPosition;
		if (isValid() == true) {
			return mJvListData.get(mCurrentPosition);
		} else {
			mCurrentPosition = nextCurrentPosition;
			sbErrMessage.append("다음 단어가 없습니다.");
		}

		return null;
	}

	private boolean isValid() {
		if (mJvListData == null) {
			return false;
		}

		if (mCurrentPosition < 0 || mCurrentPosition >= mJvListData.size()) {
			return false;
		}

		return true;
	}
	
}
