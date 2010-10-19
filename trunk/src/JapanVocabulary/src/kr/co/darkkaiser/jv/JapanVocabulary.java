package kr.co.darkkaiser.jv;

import java.util.Calendar;

import android.text.TextUtils;

public class JapanVocabulary {

	private long mIdx = -1;

	// 일본식 한자 단어
	private String mVocabulary = null;

	// 일본식 한자 단어에 대한 히라가나/가타가나
	private String mVocabularyGana = null;

	// 일본식 한자 단어에 대한 뜻
	private String mVocabularyTranslation = null;

	// 단어가 등록된 날짜(UTC)
	private long mRegistrationDateUTC;

	// 단어 암기 대상 여부
	private boolean mMemorizeTarget = false;

	// 단어 암기 완료 여부
	private boolean mMemorizeCompleted = false;
	
	// 단어 암기 완료 횟수
	private long mMemorizeCompletedCount = 0;
	
	// 단어의 품사 
	private long mPartsOfSpeech = 99/* 지정된 품사 없음 */;

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
		return String.format("%04d년 %02d월 %02d일", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
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
