package kr.co.darkkaiser.jv.data;

import java.util.Calendar;

import android.text.TextUtils;

public class JapanVocabulary {

	private long mIdx = -1;

	// 일본식 한자 단어
	private String mVocabulary = null;

	// 일본식 한자 단어에 대한 히라가나/가타카나
	private String mVocabularyGana = null;

	// 일본식 한자 단어에 대한 뜻
	private String mVocabularyTranslation = null;

	// 단어가 등록된 날짜(UTC)
	private long mRegistrationDateUTC;

	// 단어 암기 대상 여부
	private boolean mMemorizeTarget = true;

	// 단어 암기 완료 여부
	private boolean mMemorizeCompleted = false;
	
	// 단어 암기 완료 횟수
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
		// 최초 1회만 초기화가 이루어지도록 한다.
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
