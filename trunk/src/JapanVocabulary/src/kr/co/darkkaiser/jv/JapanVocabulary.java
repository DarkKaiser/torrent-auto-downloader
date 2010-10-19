package kr.co.darkkaiser.jv;

import java.util.Calendar;

import android.text.TextUtils;

public class JapanVocabulary {

	private long mIdx = -1;

	// 단어가 등록된 날짜(UTC)
	private long mRegistrationUTCDate;

	// 한자 단어
	private String mVocabulary = null;

	// 한자 단어에 대한 히라가나/가타가나
	private String mVocabularyGana = null;

	// 단어에 대한 뜻
	private String mVocabularyTranslation = null;

	// 암기 대상 단어인지에 대한 플래그
	private boolean mIsMemorizeTarget = true;

	// 단어 암기가 완료되었는지의 대한 플래그
	private boolean mIsMemorizeCompleted = false;

	public JapanVocabulary(long idx, long utcDateTime, String vocabulary, String vocabularyGana, String vocabularyTranslation) {
		this(idx, utcDateTime, vocabulary, vocabularyGana, vocabularyTranslation, true, false);
	}

	public JapanVocabulary(long idx, long utcDateTime, String vocabulary, String vocabularyGana, String vocabularyTranslation, boolean isMemorizeTarget, boolean isMemorizeCompleted) {
		assert idx != -1;
		assert utcDateTime > 0;
		assert TextUtils.isEmpty(vocabulary) == false;
		assert TextUtils.isEmpty(vocabularyGana) == false;
		assert TextUtils.isEmpty(vocabularyTranslation) == false;
		
		mIdx = idx;

		setVocabulary(vocabulary);
		setVocabularyGana(vocabularyGana);
		setVocabularyTranslation(vocabularyTranslation);
		setMemorizeTarget(isMemorizeTarget, false);
		setMemorizeCompleted(isMemorizeCompleted, false);
		setRegistrationDate(utcDateTime);
	}
	
	public long getIdx() {
		return mIdx;
	}

	public long getRegistrationDate() {
		return mRegistrationUTCDate;
	}
	
	public String getRegistrationDateString() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mRegistrationUTCDate);
		return String.format("%04d년 %02d월 %02d일", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
	}

	private void setRegistrationDate(long utcDateTime) {
		assert utcDateTime > 0;
		mRegistrationUTCDate = utcDateTime;
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
		return mIsMemorizeTarget;
	}
	
	public void setMemorizeTarget(boolean flag, boolean updateToDB) {
		mIsMemorizeTarget = flag;

		if (updateToDB == true) {
			assert mIdx != -1;
			JapanVocabularyManager.getInstance().updateMemorizeTarget(mIdx, flag);
		}
	}

	public boolean isMemorizeCompleted() {
		return mIsMemorizeCompleted;
	}
	
	public void setMemorizeCompleted(boolean flag, boolean updateToDB) {
		mIsMemorizeCompleted = flag;
		
		if (updateToDB == true) {
			assert mIdx != -1;
			JapanVocabularyManager.getInstance().updateMemorizeCompleted(mIdx, flag);
		}
	}
	
}
