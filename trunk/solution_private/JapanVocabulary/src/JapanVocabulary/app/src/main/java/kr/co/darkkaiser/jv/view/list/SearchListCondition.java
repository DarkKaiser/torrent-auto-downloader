package kr.co.darkkaiser.jv.view.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import kr.co.darkkaiser.jv.R;

public class SearchListCondition {

	private static final String SPKEY_AVSL_SEARCH_WORD = "avsl_search_word";
    private static final String SPKEY_AVSL_MEMORIZE_TARGET = "avsl_memorize_target";
    private static final String SPKEY_AVSL_MEMORIZE_COMPLETED = "avsl_memorize_completed";

    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;

    private String mSearchWord = null;
    private int mMemorizeTarget = 0;
    private int mMemorizeCompleted = 0;





	private static final String JV_SPN_CHECKED_JLPT_LEVEL_SC = "sc_jlpt_level";
	private boolean[] mScCheckedJLPTLevelArray = null;

    //@@@@@
	public SearchListCondition(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

		mContext = context;
		mSharedPreferences = sharedPreferences;

		mSearchWord = mSharedPreferences.getString(SPKEY_AVSL_SEARCH_WORD, "");
		mMemorizeTarget = mSharedPreferences.getInt(SPKEY_AVSL_MEMORIZE_TARGET, 0/* 전체 단어 */);
		mMemorizeCompleted = mSharedPreferences.getInt(SPKEY_AVSL_MEMORIZE_COMPLETED, 0/* 전체 단어 */);

		// JLPT 각 급수별 검색 여부 플래그를 읽어들인다.
		CharSequence[] jlptLevelList = mContext.getResources().getTextArray(R.array.sc_jlpt_level_list);
		CharSequence[] jlptLevelListValues = mContext.getResources().getTextArray(R.array.sc_jlpt_level_list_values);

		assert jlptLevelList.length == jlptLevelListValues.length;

		mScCheckedJLPTLevelArray = new boolean[jlptLevelList.length];
		for (int index = 0; index < jlptLevelList.length; ++index) {
			mScCheckedJLPTLevelArray[index] = mSharedPreferences.getBoolean(String.format("%s_%s", JV_SPN_CHECKED_JLPT_LEVEL_SC, jlptLevelListValues[index]), true);
		}
	}

    public String getSearchWord() {
		return mSearchWord;
	}

    public void setSearchWord(String searchWord) {
		assert mSharedPreferences != null;
		mSearchWord = searchWord;
	}

    public int getMemorizeTarget() {
		return mMemorizeTarget;
	}

    //@@@@@
    public void setMemorizeTargetPosition(int scPosition) {
		assert mSharedPreferences != null;
		
		mMemorizeTarget = scPosition;
	}

    public int getMemorizeCompleted() {
		return mMemorizeCompleted;
	}

    //@@@@@
    public void setMemorizeCompletedPosition(int scPosition) {
		assert mSharedPreferences != null;
		
		mMemorizeCompleted = scPosition;
	}

    //@@@@@
    public boolean [] getCheckedJLPTLevelArray() {
		assert mContext != null;
		assert mScCheckedJLPTLevelArray != null;
	
		return mScCheckedJLPTLevelArray;
	}

    //@@@@@
    public void setCheckedJLPTLevel(int position, boolean value) {
		assert mSharedPreferences != null;

		if (mScCheckedJLPTLevelArray != null && mScCheckedJLPTLevelArray.length > position)
			mScCheckedJLPTLevelArray[position] = value;
		else
			assert false;
	}

    //@@@@@
    public void commit() {
        Editor editor = mSharedPreferences.edit();
        assert editor != null;

        editor.putString(SPKEY_AVSL_SEARCH_WORD, mSearchWord);
        editor.putInt(SPKEY_AVSL_MEMORIZE_TARGET, mMemorizeTarget);
        editor.putInt(SPKEY_AVSL_MEMORIZE_COMPLETED, mMemorizeCompleted);

        // JLPT 각 급수별 검색 여부 플래그를 저장한다.
        CharSequence[] jlptLevelList = mContext.getResources().getTextArray(R.array.sc_jlpt_level_list);
        CharSequence[] jlptLevelListValues = mContext.getResources().getTextArray(R.array.sc_jlpt_level_list_values);

        assert jlptLevelList.length == jlptLevelListValues.length;
        assert jlptLevelList.length == mScCheckedJLPTLevelArray.length;

        for (int index = 0; index < jlptLevelList.length; ++index) {
            editor.putBoolean(String.format("%s_%s", JV_SPN_CHECKED_JLPT_LEVEL_SC, jlptLevelListValues[index]), mScCheckedJLPTLevelArray[index]);
        }

        editor.commit();
    }

}
