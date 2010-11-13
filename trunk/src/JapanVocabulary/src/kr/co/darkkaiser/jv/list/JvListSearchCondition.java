package kr.co.darkkaiser.jv.list;

import kr.co.darkkaiser.jv.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class JvListSearchCondition {

	private static final String JV_SPN_SEARCH_WORD_SC = "sc_search_word";
	private static final String JV_SPN_MEMORIZE_TARGET_SC = "sc_memorize_target_position";
	private static final String JV_SPN_MEMORIZE_COMPLETED_SC = "sc_memorize_completed_position";
	private static final String JV_SPN_ALL_REG_DATE_SEARCH_SC = "sc_all_reg_date_search";
	private static final String JV_SPN_FIRST_SEARCH_DATE_SC = "sc_first_search_date";
	private static final String JV_SPN_LAST_SEARCH_DATE_SC = "sc_last_search_date";
	private static final String JV_SPN_CHECKED_JLPT_LEVEL_SC = "sc_jlpt_level";

	private Context mContext = null;
	private SharedPreferences mPreferences = null;

	private String mScSearchWord = null;
	private int mScMemorizeTargetPosition = 0;
	private int mScMemorizeCompletedPosition = 0;
	private boolean mScAllRegDateSearch = true;
	private String mScFirstSearchDate = null;
	private String mScLastSearchDate = null;
	private boolean[] mScCheckedJLPTLevelArray = null;

	public JvListSearchCondition(Context context, SharedPreferences preferences) {
		mContext = context;
		mPreferences = preferences;

		mScSearchWord = mPreferences.getString(JV_SPN_SEARCH_WORD_SC, "");
		mScMemorizeTargetPosition = mPreferences.getInt(JV_SPN_MEMORIZE_TARGET_SC, 0);
		mScMemorizeCompletedPosition = mPreferences.getInt(JV_SPN_MEMORIZE_COMPLETED_SC, 0);
		mScAllRegDateSearch = mPreferences.getBoolean(JV_SPN_ALL_REG_DATE_SEARCH_SC, true);
		mScFirstSearchDate = mPreferences.getString(JV_SPN_FIRST_SEARCH_DATE_SC, "");
		mScLastSearchDate = mPreferences.getString(JV_SPN_LAST_SEARCH_DATE_SC, "");

		if (TextUtils.isEmpty(mScFirstSearchDate) == true || TextUtils.isEmpty(mScLastSearchDate) == true) {
			mScAllRegDateSearch = true;
		}

		// JLPT 각 급수별 검색 여부 플래그를 읽어들인다.
		CharSequence[] jlptLevelList = mContext.getResources().getTextArray(R.array.sc_jlpt_level_list);
		CharSequence[] jlptLevelListValues = mContext.getResources().getTextArray(R.array.sc_jlpt_level_list_values);

		assert jlptLevelList.length == jlptLevelListValues.length;

		mScCheckedJLPTLevelArray = new boolean[jlptLevelList.length];
		for (int index = 0; index < jlptLevelList.length; ++index) {
			mScCheckedJLPTLevelArray[index] = mPreferences.getBoolean(String.format("%s_%s", JV_SPN_CHECKED_JLPT_LEVEL_SC, jlptLevelListValues[index]), true);
		}
	}

	public String getSearchWord() {
		return mScSearchWord;
	}

	public void setSearchWord(String searchWord) {
		assert mPreferences != null;
		
		mScSearchWord = searchWord;
	}

	public int getMemorizeTargetPosition() {
		return mScMemorizeTargetPosition;
	}

	public void setMemorizeTargetPosition(int scPosition) {
		assert mPreferences != null;
		
		mScMemorizeTargetPosition = scPosition;
	}

	public int getMemorizeCompletedPosition() {
		return mScMemorizeCompletedPosition;
	}

	public void setMemorizeCompletedPosition(int scPosition) {
		assert mPreferences != null;
		
		mScMemorizeCompletedPosition = scPosition;
	}
	
	public boolean isAllRegDateSearch() {
		return mScAllRegDateSearch;
	}

	public void setAllRegDateSearch(boolean flag) {
		assert mPreferences != null;
		
		mScAllRegDateSearch = flag;
	}
	
	public String getFirstSearchDate() {
		return mScFirstSearchDate;
	}
	
	public String getLastSearchDate() {
		return mScLastSearchDate;
	}

	public void setSearchDateRange(String firstSearchDate, String lastSearchDate) {
		assert mPreferences != null;

		mScFirstSearchDate = firstSearchDate;
		mScLastSearchDate = lastSearchDate;
	}

	public boolean [] getCheckedJLPTLevelArray() {
		assert mContext != null;
		assert mScCheckedJLPTLevelArray != null;
	
		return mScCheckedJLPTLevelArray;
	}

	public void setCheckedJLPTLevel(int position, boolean value) {
		assert mPreferences != null;

		if (mScCheckedJLPTLevelArray != null && mScCheckedJLPTLevelArray.length > position)
			mScCheckedJLPTLevelArray[position] = value;
		else
			assert false;
	}

	public void commit() {
		Editor editor = mPreferences.edit();
		assert editor != null;

		editor.putString(JV_SPN_SEARCH_WORD_SC, mScSearchWord);		
		editor.putInt(JV_SPN_MEMORIZE_TARGET_SC, mScMemorizeTargetPosition);
		editor.putInt(JV_SPN_MEMORIZE_COMPLETED_SC, mScMemorizeCompletedPosition);
		editor.putBoolean(JV_SPN_ALL_REG_DATE_SEARCH_SC, mScAllRegDateSearch);
		editor.putString(JV_SPN_FIRST_SEARCH_DATE_SC, mScFirstSearchDate);
		editor.putString(JV_SPN_LAST_SEARCH_DATE_SC, mScLastSearchDate);

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
