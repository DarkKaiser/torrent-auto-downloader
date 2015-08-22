package kr.co.darkkaiser.jv.view.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.List;

import kr.co.darkkaiser.jv.R;

public class SearchListCondition {

    public enum MemorizeTarget {
        ALL,
        MEMORIZE_TARGET,
        MEMORIZE_NON_TARGET;

        public static MemorizeTarget parseMemorizeTarget(int ordinal) {
            if (ordinal == MemorizeTarget.MEMORIZE_TARGET.ordinal())
                return MemorizeTarget.MEMORIZE_TARGET;
            else if (ordinal == MemorizeTarget.MEMORIZE_NON_TARGET.ordinal())
                return MemorizeTarget.MEMORIZE_NON_TARGET;

            return MemorizeTarget.ALL;
        }
    }

    public enum MemorizeCompleted {
        ALL,
        MEMORIZE_COMPLETED,
        MEMORIZE_UNCOMPLETED;

        public static MemorizeCompleted parseMemorizeCompleted(int ordinal) {
            if (ordinal == MemorizeCompleted.MEMORIZE_COMPLETED.ordinal())
                return MemorizeCompleted.MEMORIZE_COMPLETED;
            else if (ordinal == MemorizeCompleted.MEMORIZE_UNCOMPLETED.ordinal())
                return MemorizeCompleted.MEMORIZE_UNCOMPLETED;

            return MemorizeCompleted.ALL;
        }
    }

    public enum JLPTRanking {
        N1("01"),
        N2("02"),
        N3("03"),
        N4("04"),
        N5("05"),
        UNCLASSIFIED("99");

        private String code;

        JLPTRanking(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

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
    }

    private static final String SPKEY_AVSL_SEARCH_WORD = "avsl_search_word";
    private static final String SPKEY_AVSL_MEMORIZE_TARGET = "avsl_memorize_target";
    private static final String SPKEY_AVSL_MEMORIZE_COMPLETED = "avsl_memorize_completed";
    private static final String SPKEY_AVSL_JLPT_RANKING = "avsl_jlpt_ranking";

    private Context context = null;
    private SharedPreferences sharedPreferences = null;

    private String searchWord = null;
    private MemorizeTarget memorizeTarget = null;
    private MemorizeCompleted memorizeCompleted = null;
	private boolean[] jlptRankingArray = null;

    public SearchListCondition(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

		this.context = context;
		this.sharedPreferences = sharedPreferences;

		this.searchWord = this.sharedPreferences.getString(SPKEY_AVSL_SEARCH_WORD, "");
        this.memorizeTarget = MemorizeTarget.valueOf(this.sharedPreferences.getString(SPKEY_AVSL_MEMORIZE_TARGET, MemorizeTarget.ALL.name()));
		this.memorizeCompleted = MemorizeCompleted.valueOf(this.sharedPreferences.getString(SPKEY_AVSL_MEMORIZE_COMPLETED, MemorizeCompleted.ALL.name()));

		// JLPT 급수별 검색여부를 읽어들인다.
		CharSequence[] aJLPTRanking = this.context.getResources().getTextArray(R.array.search_condition_jlpt_ranking);

        this.jlptRankingArray = new boolean[aJLPTRanking.length];
		for (int index = 0; index < aJLPTRanking.length; ++index)
            this.jlptRankingArray[index] = this.sharedPreferences.getBoolean(String.format("%s_%s", SPKEY_AVSL_JLPT_RANKING, JLPTRanking.parseJLPTRanking(index)), true);
	}

    public String getSearchWord() {
		return this.searchWord;
	}

    public void setSearchWord(String searchWord) {
		assert this.sharedPreferences != null;
        this.searchWord = searchWord;
	}

    public MemorizeTarget getMemorizeTarget() {
		return this.memorizeTarget;
	}

    public void setMemorizeTarget(MemorizeTarget memorizeTarget) {
        this.memorizeTarget = memorizeTarget;
	}

    public MemorizeCompleted getMemorizeCompleted() {
		return this.memorizeCompleted;
	}

    public void setMemorizeCompleted(MemorizeCompleted memorizeCompleted) {
        this.memorizeCompleted = memorizeCompleted;
	}

    public String[] getJLPTRankingNames() {
        assert this.context != null;
        return this.context.getResources().getStringArray(R.array.search_condition_jlpt_ranking);
    }

    public ArrayList<Integer> getJLPTRankingSelectedIndicies() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int index = 0; index < this.jlptRankingArray.length; ++index) {
            if (this.jlptRankingArray[index] == true)
                result.add(index);
        }

		return result;
	}

    public void setJLPTRanking(List<Integer> selectedIndicies) {
        for (int index = 0; index < this.jlptRankingArray.length; ++index)
            this.jlptRankingArray[index] = false;

        for (Integer index : selectedIndicies) {
            if (index >= 0 && index < this.jlptRankingArray.length)
                this.jlptRankingArray[index] = true;
            else
                assert false;
        }
    }

    public void commit() {
        assert this.sharedPreferences != null;

        Editor editor = this.sharedPreferences.edit();
        assert editor != null;

        editor.putString(SPKEY_AVSL_SEARCH_WORD, this.searchWord);
        editor.putString(SPKEY_AVSL_MEMORIZE_TARGET, this.memorizeTarget.name());
        editor.putString(SPKEY_AVSL_MEMORIZE_COMPLETED, this.memorizeCompleted.name());

        // JLPT 각 급수별 검색 여부 플래그를 저장한다.
        for (int index = 0; index < this.jlptRankingArray.length; ++index)
            editor.putBoolean(String.format("%s_%s", SPKEY_AVSL_JLPT_RANKING, JLPTRanking.parseJLPTRanking(index)), this.jlptRankingArray[index]);

        editor.commit();
    }

}
