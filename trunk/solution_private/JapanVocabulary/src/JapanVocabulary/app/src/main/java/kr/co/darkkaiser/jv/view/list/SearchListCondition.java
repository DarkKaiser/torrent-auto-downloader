package kr.co.darkkaiser.jv.view.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import kr.co.darkkaiser.jv.R;

public class SearchListCondition {

    public enum MemorizeTarget {
        ALL,
        MEMORIZE_TARGET,
        MEMORIZE_NON_TARGET
    }

    public enum MemorizeCompleted {
        ALL,
        MEMORIZE_COMPLETED,
        MEMORIZE_UNCOMPLETED
    }

    public enum JLPTRanking {
        N1("01", "N1"),
        N2("02", "N2"),
        N3("03", "N3"),
        N4("04", "N4"),
        N5("05", "N5"),
        UNCLASSIFIED("99", "미분류");

        private String mCode;
        private String mName;

        public static JLPTRanking parseJLPTRanking(int ordinal) {
            if (ordinal == JLPTRanking.N1.ordinal())
                return JLPTRanking.N1;
            else if (ordinal == JLPTRanking.N2.ordinal())
                return JLPTRanking.N2;
            else if (ordinal == JLPTRanking.N3.ordinal())
                return JLPTRanking.N3;
            else if (ordinal == JLPTRanking.N4.ordinal())
                return JLPTRanking.N4;
            else if (ordinal == JLPTRanking.N5.ordinal())
                return JLPTRanking.N5;

            return JLPTRanking.UNCLASSIFIED;
        }

        JLPTRanking(String code, String name) {
            mCode = code;
            mName = name;
        }

        public String getCode() {
            return mCode;
        }

        public String getName() {
            return mName;
        }
    }

    private static final String SPKEY_AVSL_SEARCH_WORD = "avsl_search_word";
    private static final String SPKEY_AVSL_MEMORIZE_TARGET = "avsl_memorize_target";
    private static final String SPKEY_AVSL_MEMORIZE_COMPLETED = "avsl_memorize_completed";
    private static final String SPKEY_AVSL_JLPT_RANKING = "avsl_jlpt_ranking";

    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;

    private String mSearchWord = null;
    private MemorizeTarget mMemorizeTarget = null;
    private MemorizeCompleted mMemorizeCompleted = null;
	private boolean[] mJLPTRankingArray = null;

	public SearchListCondition(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

		mContext = context;
		mSharedPreferences = sharedPreferences;

		mSearchWord = mSharedPreferences.getString(SPKEY_AVSL_SEARCH_WORD, "");
        mMemorizeTarget = MemorizeTarget.valueOf(mSharedPreferences.getString(SPKEY_AVSL_MEMORIZE_TARGET, MemorizeTarget.ALL.name()));
		mMemorizeCompleted = MemorizeCompleted.valueOf(mSharedPreferences.getString(SPKEY_AVSL_MEMORIZE_COMPLETED, MemorizeCompleted.ALL.name()));

		// JLPT 급수별 검색여부를 읽어들인다.
		CharSequence[] aJLPTRanking = mContext.getResources().getTextArray(R.array.search_condition_jlpt_ranking);

        mJLPTRankingArray = new boolean[aJLPTRanking.length];
		for (int index = 0; index < aJLPTRanking.length; ++index)
            mJLPTRankingArray[index] = mSharedPreferences.getBoolean(String.format("%s_%s", SPKEY_AVSL_JLPT_RANKING, JLPTRanking.parseJLPTRanking(index)), true);
	}

    public String getSearchWord() {
		return mSearchWord;
	}

    public void setSearchWord(String searchWord) {
		assert mSharedPreferences != null;
		mSearchWord = searchWord;
	}

    public MemorizeTarget getMemorizeTarget() {
		return mMemorizeTarget;
	}

    public void setMemorizeTarget(MemorizeTarget memorizeTarget) {
		mMemorizeTarget = memorizeTarget;
	}

    public MemorizeCompleted getMemorizeCompleted() {
		return mMemorizeCompleted;
	}

    public void setMemorizeCompleted(MemorizeCompleted memorizeCompleted) {
		mMemorizeCompleted = memorizeCompleted;
	}

    public boolean[] getJLPTRankingArray() {
		return mJLPTRankingArray;
	}

    public void setJLPTRanking(int position, boolean value) {
		if (mJLPTRankingArray != null && mJLPTRankingArray.length > position)
			mJLPTRankingArray[position] = value;
		else
			assert false;
	}

    public void commit() {
        Editor editor = mSharedPreferences.edit();
        assert editor != null;

        editor.putString(SPKEY_AVSL_SEARCH_WORD, mSearchWord);
        editor.putString(SPKEY_AVSL_MEMORIZE_TARGET, mMemorizeTarget.name());
        editor.putString(SPKEY_AVSL_MEMORIZE_COMPLETED, mMemorizeCompleted.name());

        // JLPT 각 급수별 검색 여부 플래그를 저장한다.
        CharSequence[] aJLPTRanking = mContext.getResources().getTextArray(R.array.search_condition_jlpt_ranking);
        assert aJLPTRanking.length == mJLPTRankingArray.length;

        for (int index = 0; index < aJLPTRanking.length; ++index)
            editor.putBoolean(String.format("%s_%s", SPKEY_AVSL_JLPT_RANKING, JLPTRanking.parseJLPTRanking(index)), mJLPTRankingArray[index]);

        editor.commit();
    }

}
