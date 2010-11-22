package kr.co.darkkaiser.jv.data;

import java.util.Calendar;

import android.text.TextUtils;

public class JapanVocabulary {

	private long mIdx = -1;

	// �Ϻ��� ���� �ܾ�
	private String mVocabulary = null;

	// �Ϻ��� ���� �ܾ ���� ���󰡳�/��Ÿī��
	private String mVocabularyGana = null;

	// �Ϻ��� ���� �ܾ ���� ��
	private String mVocabularyTranslation = null;

	// �ܾ ��ϵ� ��¥(UTC)
	private long mRegistrationDateUTC;

	// �ܾ� �ϱ� ��� ����
	private boolean mMemorizeTarget = true;

	// �ܾ� �ϱ� �Ϸ� ����
	private boolean mMemorizeCompleted = false;
	
	// �ܾ� �ϱ� �Ϸ� Ƚ��
	private long mMemorizeCompletedCount = 0;

	public JapanVocabulary(long idx, long utcDateTime, String vocabulary, String vocabularyGana, String vocabularyTranslation) {
		assert idx != -1;
		assert utcDateTime > 0;
		assert TextUtils.isEmpty(vocabulary) == false;
		assert TextUtils.isEmpty(vocabularyGana) == false;
		assert TextUtils.isEmpty(vocabularyTranslation) == false;

		mIdx = idx;

		setVocabulary(vocabulary);
		setVocabularyGana(vocabularyGana);
		setVocabularyTranslation(vocabularyTranslation);
		setRegistrationDate(utcDateTime);
	}

	public long getIdx() {
		return mIdx;
	}

	public long getRegistrationDate() {
		return mRegistrationDateUTC;
	}
	
	public String getRegistrationDateString() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mRegistrationDateUTC);
		return String.format("%04d�� %02d�� %02d��", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
	}

	private void setRegistrationDate(long utcDateTime) {
		assert utcDateTime > 0;
		mRegistrationDateUTC = utcDateTime;
	}

	public String getVocabulary() {
		return mVocabulary;
	}

	private void setVocabulary(String vocabulary) {
		assert TextUtils.isEmpty(vocabulary) == false;
		mVocabulary = vocabulary;
	}

	public String getVocabularyGana() {
		return mVocabularyGana;
	}

	private void setVocabularyGana(String vocabularyGana) {
		assert TextUtils.isEmpty(vocabularyGana) == false;
		mVocabularyGana = vocabularyGana;
	}

	public String getVocabularyTranslation() {
		return mVocabularyTranslation;
	}
	
	private void setVocabularyTranslation(String vocabularyTranslation) {
		assert TextUtils.isEmpty(vocabularyTranslation) == false;
		mVocabularyTranslation = vocabularyTranslation;
	}
	
	public boolean isMemorizeTarget() {
		return mMemorizeTarget;
	}
	
	public void setMemorizeTarget(boolean flag, boolean updateToDB) {
		mMemorizeTarget = flag;

		if (updateToDB == true) {
			JvManager.getInstance().writeUserVocabularyInfo();
		}
	}

	public boolean isMemorizeCompleted() {
		return mMemorizeCompleted;
	}

	public void setMemorizeCompleted(boolean flag, boolean whileAddMemorizeCompletedCount, boolean updateToDB) {
		if (flag == true && mMemorizeCompleted == false && whileAddMemorizeCompletedCount == true)
			++mMemorizeCompletedCount;

		mMemorizeCompleted = flag;

		if (updateToDB == true) {
			JvManager.getInstance().writeUserVocabularyInfo();
		}
	}

	public void setFirstOnceMemorizeCompletedCount(long count) {
		// ���� 1ȸ�� �ʱ�ȭ�� �̷�������� �Ѵ�.
		if (mMemorizeCompletedCount == 0) {
			mMemorizeCompletedCount = count;
		} else {
			assert false;
		}
	}

	public long getMemorizeCompletedCount() {
		return mMemorizeCompletedCount;
	}

}
