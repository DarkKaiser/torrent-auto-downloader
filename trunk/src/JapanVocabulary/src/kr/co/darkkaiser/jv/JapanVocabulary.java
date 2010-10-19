package kr.co.darkkaiser.jv;

import java.util.Calendar;

import android.text.TextUtils;

public class JapanVocabulary {

	private long mIdx = -1;

	// �Ϻ��� ���� �ܾ�
	private String mVocabulary = null;

	// �Ϻ��� ���� �ܾ ���� ���󰡳�/��Ÿ����
	private String mVocabularyGana = null;

	// �Ϻ��� ���� �ܾ ���� ��
	private String mVocabularyTranslation = null;

	// �ܾ ��ϵ� ��¥(UTC)
	private long mRegistrationDateUTC;

	// �ܾ� �ϱ� ��� ����
	private boolean mMemorizeTarget = false;

	// �ܾ� �ϱ� �Ϸ� ����
	private boolean mMemorizeCompleted = false;
	
	// �ܾ� �ϱ� �Ϸ� Ƚ��
	private long mMemorizeCompletedCount = 0;
	
	// �ܾ��� ǰ�� 
	private long mPartsOfSpeech = 99/* ������ ǰ�� ���� */;

	public JapanVocabulary(long idx, long utcDateTime, String vocabulary, String vocabularyGana, String vocabularyTranslation, long partsOfSpeech) {
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
		setPartsOfSpeech(partsOfSpeech);
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
	
	public long getPartsOfSpeech() {
		return mPartsOfSpeech;
	}

	private void setPartsOfSpeech(long partsOfSpeech) {
		mPartsOfSpeech = partsOfSpeech;
	}

	public boolean isMemorizeTarget() {
		return mMemorizeTarget;
	}
	
	// @@@@@
	public void setMemorizeTarget(boolean flag, boolean updateToDB) {
		mMemorizeTarget = flag;

		if (updateToDB == true) {
			assert mIdx != -1;
			//JvManager.getInstance().updateMemorizeTarget(mIdx, flag);
		}
	}

	public boolean isMemorizeCompleted() {
		return mMemorizeCompleted;
	}
	
	// @@@@@
	public void setMemorizeCompleted(boolean flag, boolean updateToDB) {
		mMemorizeCompleted = flag;
		++mMemorizeCompletedCount;
		
		if (updateToDB == true) {
			assert mIdx != -1;
			//JvManager.getInstance().updateMemorizeCompleted(mIdx, flag);
		}
	}
	
	public long getMemorizeCompletedCount() {
		return mMemorizeCompletedCount;
	}

}
