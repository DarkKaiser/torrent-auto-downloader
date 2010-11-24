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

	// �ϱ� ��� �ܾ� ����Ʈ
	private ArrayList<JapanVocabulary> mJvList = new ArrayList<JapanVocabulary>();

	// �ϱ� ��� �ܾ� ����Ʈ �߿��� �ϱ� �Ϸ��� �ܾ��� ����
	private int mMemorizeCompletedCount = 0;

	// ���� ȭ�鿡 �������� �ִ� �ϱ� ��� �ܾ��� ��ġ
	private int mCurrentPosition = -1;

	// (ȯ�漳�� ��)�ܾ� �ϱ� ����(������, ����(...))
	private int mMemorizeOrderMethod = 0/* ���� */;

	// (ȯ�漳�� ��)ȭ�鿡 ��� �� �ϱ� ��� �ܾ��� �׸�(����, ���󰡳�/��Ÿ����)
	private JvMemorizeTargetItem mJvMemorizeTargetItem = JvMemorizeTargetItem.VOCABULARY;

	public JvMemorizeList() {
		
	}

	public boolean reloadPreference(SharedPreferences preferences) {
		assert preferences != null;

		// ȭ�鿡 ��� �� �ϱ� ��� �ܾ��� �׸��� �д´�.
		String memorizeTargetItem = preferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (TextUtils.equals(memorizeTargetItem, "0") == true)
			mJvMemorizeTargetItem = JvMemorizeTargetItem.VOCABULARY;
		else
			mJvMemorizeTargetItem = JvMemorizeTargetItem.VOCABULARY_GANA;

		// �ܾ� �ϱ� ������ �д´�.
		int prevMemorizeOrderMethod = mMemorizeOrderMethod;
		mMemorizeOrderMethod = Integer.parseInt(preferences.getString(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD, "0"));

		return (mMemorizeOrderMethod != prevMemorizeOrderMethod ? true : false);
	}

	public synchronized void loadData() {
		// �ϱ� ��� �ܾ�鸸�� ���͸��Ѵ�.
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
		// ������ �ܾ� �ε����� �����Ѵ�. currentIndex
//		if (mJvMemorizeRandomMode == false) {
//			// ������ �ܾ� �ε����� �����Ѵ�. @@@@@
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
			mCurrentPosition = -1;//???? ��������϶� ÷���� ���ư��� �̻����� �ʳ�?
			sbErrMessage.append("�ϱ� �� �ܾ �����ϴ�.");
		} else {
			if (mMemorizeOrderMethod == 0/* ���� */) {
//				// @@@@@ �������� �����ֱ� ���� ���� �ܾ� ����
//				
//				// @@@@@ ������ ���ڸ� ���� �� ���������� ������ ���鼭 ã�´�.
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
//					Toast.makeText(this, "���� �ܾ �����ϴ�.", Toast.LENGTH_SHORT).show();
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
//		jvInfo.setText(String.format("�ϱ�Ϸ� %d�� / �ϱ��� %d��", mJvMemorizeCompletedCount, mJvList.size()));
		// JvActivity���� ���ڿ� ������ �ؾ����� �ʳ�?

		return null;
	}
}
