package kr.co.darkkaiser.jv.view.list;

import kr.co.darkkaiser.jv.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SearchListCondition {

	private static final String JV_SPN_SEARCH_WORD_SC = "sc_search_word";
	private static final String JV_SPN_MEMORIZE_TARGET_SC = "sc_memorize_target_position";
	private static final String JV_SPN_MEMORIZE_COMPLETED_SC = "sc_memorize_completed_position";
	private static final String JV_SPN_CHECKED_JLPT_LEVEL_SC = "sc_jlpt_level";

	private Context mContext = null;
	private SharedPreferences mSharedPreferences = null;

	private String mSearchWord = null;
	private int mScMemorizeTargetPosition = 0;
	private int mScMemorizeCompletedPosition = 0;
	private boolean[] mScCheckedJLPTLevelArray = null;

    //@@@@@
	public SearchListCondition(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

		mContext = context;
		mSharedPreferences = sharedPreferences;

		mSearchWord = mSharedPreferences.getString(JV_SPN_SEARCH_WORD_SC, "");
		mScMemorizeTargetPosition = mSharedPreferences.getInt(JV_SPN_MEMORIZE_TARGET_SC, 0);
		mScMemorizeCompletedPosition = mSharedPreferences.getInt(JV_SPN_MEMORIZE_COMPLETED_SC, 0);

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

    //@@@@@
    public void setSearchWord(String searchWord) {
		assert mSharedPreferences != null;
		
		mSearchWord = searchWord;
	}

    //@@@@@
    public int getMemorizeTargetPosition() {
		return mScMemorizeTargetPosition;
	}

    //@@@@@
    public void setMemorizeTargetPosition(int scPosition) {
		assert mSharedPreferences != null;
		
		mScMemorizeTargetPosition = scPosition;
	}

    //@@@@@
    public int getMemorizeCompletedPosition() {
		return mScMemorizeCompletedPosition;
	}

    //@@@@@
    public void setMemorizeCompletedPosition(int scPosition) {
		assert mSharedPreferences != null;
		
		mScMemorizeCompletedPosition = scPosition;
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

        editor.putString(JV_SPN_SEARCH_WORD_SC, mSearchWord);
        editor.putInt(JV_SPN_MEMORIZE_TARGET_SC, mScMemorizeTargetPosition);
        editor.putInt(JV_SPN_MEMORIZE_COMPLETED_SC, mScMemorizeCompletedPosition);

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
