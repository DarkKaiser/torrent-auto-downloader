package kr.co.darkkaiser.jv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import kr.co.darkkaiser.jv.data.JapanVocabulary;
import kr.co.darkkaiser.jv.data.JapanVocabularyComparator;
import kr.co.darkkaiser.jv.data.JapanVocabularyManager;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class JvMemorizeList {
	
	private Random mRandom = new Random();

	// 암기 대상 단어 리스트
	private ArrayList<JapanVocabulary> mJvList = new ArrayList<JapanVocabulary>();

	// 암기 대상 단어 리스트 중에서 암기 완료한 단어의 갯수
	private int mMemorizeCompletedCount = 0;

	// 현재 화면에 보여지고 있는 암기 대상 단어의 위치
	private int mCurrentPosition = -1;

	// (환경설정 값)단어 암기 순서(무작위, 순차(...))
	private int mMemorizeOrderMethod = 0/* 랜덤 */;

	// (환경설정 값)화면에 출력 할 암기 대상 단어의 항목(한자, 히라가나/가타가나)
	private JvMemorizeTargetItem mJvMemorizeTargetItem = JvMemorizeTargetItem.VOCABULARY;

	public JvMemorizeList() {
		
	}

	public boolean reloadPreference(SharedPreferences preferences) {
		assert preferences != null;

		// 화면에 출력 할 암기 대상 단어의 항목을 읽는다.
		String memorizeTargetItem = preferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (TextUtils.equals(memorizeTargetItem, "0") == true)
			mJvMemorizeTargetItem = JvMemorizeTargetItem.VOCABULARY;
		else
			mJvMemorizeTargetItem = JvMemorizeTargetItem.VOCABULARY_GANA;

		// 단어 암기 순서를 읽는다.
		int prevMemorizeOrderMethod = mMemorizeOrderMethod;
		mMemorizeOrderMethod = Integer.parseInt(preferences.getString(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD, "0"));

		return (mMemorizeOrderMethod != prevMemorizeOrderMethod ? true : false);
	}

	public synchronized void loadData() {
		// 암기 대상 단어들만을 필터링한다.
		mJvList.clear();
		mCurrentPosition = -1;
		mMemorizeCompletedCount = JapanVocabularyManager.getInstance().getMemorizeTargetJvList(mJvList);

		assert mMemorizeCompletedCount >= 0;
		assert mMemorizeCompletedCount <= mJvList.size();
	
		switch (mMemorizeOrderMethod) {
		case 1:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvVocabularyComparator);
			break;
		case 3:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvVocabularyGanaComparator);
			break;
		case 2:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvVocabularyTranslationComparator);
			break;
		case 4:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvRegistrationDateUpComparator);
			break;
		case 5:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvRegistrationDateDownComparator);
			break;
		default:
			break;
		}
	}

	public boolean isValidVocabularyPosition() {
		if (mCurrentPosition < 0)
			return false;
		
		if (mCurrentPosition >= mJvList.size())
			return false;
		
		return true;
	}

	public synchronized void setMemorizeCompletedAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			JapanVocabulary jpVocabulary = mJvList.get(mCurrentPosition);
			if (jpVocabulary != null && jpVocabulary.isMemorizeCompleted() == false) {
				++mMemorizeCompletedCount;
				jpVocabulary.setMemorizeCompleted(true, true, true);							
			}
		} else {
			assert false;
		}
	}

	public synchronized long getIdxAtVocabularyPosition() {
		if (isValidVocabularyPosition() == true) {
			mJvList.get(mCurrentPosition).getIdx();
		} else {
			assert false;
		}
		
		return -1;
	}

	public void storeVocabularyPosition() {
		// @@@@@
		// 현재의 단어 인덱스를 저장한다. currentIndex
//		if (mJvMemorizeRandomMode == false) {
//			// 현재의 단어 인덱스를 저장한다. @@@@@
//		}
	}
	
	public synchronized JapanVocabulary getCurrentVocabulary() {
		if (isValidVocabularyPosition() == true) {
			return mJvList.get(mCurrentPosition);
		}

		return null;
	}
	
	public synchronized JapanVocabulary previousVocabulary(StringBuilder sbErrorMessage) {
		// @@@@@
		return null;
	}

	// @@@@@
	public synchronized JapanVocabulary nextVocabulary(StringBuilder sbErrMessage) {
		assert sbErrMessage != null;
		
		if (mJvList.isEmpty() == true || mMemorizeCompletedCount >= mJvList.size()) {
			mCurrentPosition = -1;//???? 순차모들일때 첨으로 돌아가면 이상하지 않나?
			sbErrMessage.append("암기 할 단어가 없습니다.");
		} else {
			if (mMemorizeOrderMethod == 0/* 랜덤 */) {
//				// @@@@@ 이전으로 보여주기 위한 현재 단어 저장
//				
//				// @@@@@ 랜덤한 숫자를 구한 후 순차적으로 루프를 돌면서 찾는다.
//				if (mJvList.size() == 1) {
//					mCurrentPosition = 0;
//				} else {
//					int index = mCurrentPosition;
//
//					do
//					{
//						mCurrentPosition = mRandom.nextInt(mJvList.size());									
//					} while (mCurrentPosition == index);
//				}
			} else {
//				// @@@@@
//				if ((mCurrentPosition + 1) == mJvList.size()) {
//					Toast.makeText(this, "다음 단어가 없습니다.", Toast.LENGTH_SHORT).show();
//					return;
//				} else {
//					++mCurrentPosition;
//				}
			}

			if (isValidVocabularyPosition() == true) {
				return mJvList.get(mCurrentPosition);
			}
		}
		
		return null;
	}
	
	public JvMemorizeTargetItem getMemorizeTargetItem() {
		return mJvMemorizeTargetItem;
	}

	// @@@@@
	public StringBuilder getMemorizeVocabularyInfo() {
		assert mMemorizeCompletedCount >= 0;
		assert mMemorizeCompletedCount <= mJvList.size();

//		TextView jvInfo = (TextView)findViewById(R.id.jv_info);
//		jvInfo.setText(String.format("암기완료 %d개 / 암기대상 %d개", mJvMemorizeCompletedCount, mJvList.size()));
		// JvActivity에서 문자열 생성을 해야하지 않나?

		return null;
	}
}
