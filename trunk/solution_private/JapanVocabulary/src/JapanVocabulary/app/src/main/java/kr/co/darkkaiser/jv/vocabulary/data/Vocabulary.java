package kr.co.darkkaiser.jv.vocabulary.data;

import android.text.TextUtils;

import java.util.Calendar;

public class Vocabulary {

	private long mIdx = -1;

	// 일본식 한자 단어
	private String mVocabulary = null;

	// 일본식 한자 단어에 대한 히라가나/가타카나
	private String mVocabularyGana = null;

	// 일본식 한자 단어에 대한 뜻
	private String mVocabularyTranslation = null;

	// 단어 암기대상 여부
	private boolean mMemorizeTarget = true;

	// 단어 암기완료 여부
	private boolean mMemorizeCompleted = false;

	// 단어 암기완료 횟수
	private long mMemorizeCompletedCount = 0;

    // 단어가 등록된 날짜(UTC)
    private long mInputDateUTC;

	public Vocabulary(long idx, long utcDateTime, String vocabulary, String vocabularyGana, String vocabularyTranslation) {
		assert idx != -1;
		assert utcDateTime > 0;
		assert TextUtils.isEmpty(vocabulary) == false;
		assert TextUtils.isEmpty(vocabularyGana) == false;
		assert TextUtils.isEmpty(vocabularyTranslation) == false;

		mIdx = idx;
        mVocabulary = vocabulary;
        mVocabularyGana = vocabularyGana;
        mVocabularyTranslation = vocabularyTranslation;
        mInputDateUTC = utcDateTime;
	}

	public long getIdx() {
		return mIdx;
	}

	public String getVocabulary() {
		return mVocabulary;
	}

	public String getVocabularyGana() {
		return mVocabularyGana;
	}

	public String getVocabularyTranslation() {
		return mVocabularyTranslation;
	}

    public long getInputDate() {
        return mInputDateUTC;
    }

    public String getInputDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mInputDateUTC);
        return String.format("%04d년 %02d월 %02d일", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
    }

    public boolean isMemorizeTarget() {
		return mMemorizeTarget;
	}
	
	public void setMemorizeTarget(boolean flag) {
		mMemorizeTarget = flag;
	}

	public boolean isMemorizeCompleted() {
		return mMemorizeCompleted;
	}

	public void setMemorizeCompleted(boolean flag, boolean isWhileAddMemorizeCompletedCount) {
		if (flag == true && mMemorizeCompleted == false && isWhileAddMemorizeCompletedCount == true)
			++mMemorizeCompletedCount;

		mMemorizeCompleted = flag;
	}

	public void setMemorizeCompletedCount(long count) {
        mMemorizeCompletedCount = count;
	}

    public long getMemorizeCompletedCount() {
        return mMemorizeCompletedCount;
    }

}
