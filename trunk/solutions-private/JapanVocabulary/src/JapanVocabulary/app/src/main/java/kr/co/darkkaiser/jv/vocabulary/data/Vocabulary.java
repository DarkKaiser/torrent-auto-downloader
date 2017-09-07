package kr.co.darkkaiser.jv.vocabulary.data;

import android.text.TextUtils;

import java.util.Calendar;

import kr.co.darkkaiser.jv.BuildConfig;

// @@@@@
public class Vocabulary {

	private long idx = -1;

	// 일본식 한자 단어
	private String vocabulary = null;

	// 일본식 한자 단어에 대한 히라가나/가타카나
	private String vocabularyGana = null;

	// 일본식 한자 단어에 대한 뜻
	private String vocabularyTranslation = null;

	// 단어 암기대상 여부
	private boolean memorizeTarget = true;

	// 단어 암기완료 여부
	private boolean memorizeCompleted = false;

	// 단어 암기완료 횟수
	private long memorizeCompletedCount = 0;

    // 단어가 등록된 날짜(UTC)
    private long inputDateUTC;

	public Vocabulary(long idx, long utcDateTime, String vocabulary, String vocabularyGana, String vocabularyTranslation) {
		if (BuildConfig.DEBUG) {
			if (idx == -1) throw new RuntimeException();
			if (utcDateTime <= 0) throw new RuntimeException();
			if (TextUtils.isEmpty(vocabulary)) throw new RuntimeException();
			if (TextUtils.isEmpty(vocabularyGana)) throw new RuntimeException();
			if (TextUtils.isEmpty(vocabularyTranslation)) throw new RuntimeException();
		}

		this.idx = idx;
        this.vocabulary = vocabulary;
        this.vocabularyGana = vocabularyGana;
        this.vocabularyTranslation = vocabularyTranslation;
        this.inputDateUTC = utcDateTime;
	}

	public long getIdx() {
		return this.idx;
	}

	public String getVocabulary() {
		return this.vocabulary;
	}

	public String getVocabularyGana() {
		return this.vocabularyGana;
	}

	public String getVocabularyTranslation() {
		return this.vocabularyTranslation;
	}

    @SuppressWarnings("unused")
	public long getInputDate() {
        return this.inputDateUTC;
    }

    @SuppressWarnings("unused")
	public String getInputDateString() {
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.inputDateUTC);
        return String.format("%04d년 %02d월 %02d일", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
    }

    public boolean isMemorizeTarget() {
		return this.memorizeTarget;
	}

	public void setMemorizeTarget(boolean flag) {
        this.memorizeTarget = flag;
	}

	public boolean isMemorizeCompleted() {
		return this.memorizeCompleted;
	}

	public void setMemorizeCompleted(boolean flag, boolean isWhileAddMemorizeCompletedCount) {
		if (flag == true && this.memorizeCompleted == false && isWhileAddMemorizeCompletedCount == true)
			++this.memorizeCompletedCount;

        this.memorizeCompleted = flag;
	}

	public void setMemorizeCompletedCount(long count) {
        this.memorizeCompletedCount = count;
	}

    public long getMemorizeCompletedCount() {
        return this.memorizeCompletedCount;
    }

}
