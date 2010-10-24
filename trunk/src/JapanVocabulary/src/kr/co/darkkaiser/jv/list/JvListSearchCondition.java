package kr.co.darkkaiser.jv.list;

import android.content.SharedPreferences;

public class JvListSearchCondition {
	
	private SharedPreferences mPreferences = null;
	
	public JvListSearchCondition(SharedPreferences preferences) {
		mPreferences = preferences;
	}
	
	// @@@@@ @@@@@ JLPT(단어 디비에 있음), 품사, 암기완료/미완료/전체, 암기대상/비대상/전체, 결과내 검색??
	private String mPartsOfSpeech = null;
	private String mSearchWord = null;
	private long mSearchDateFirst = 0;
	private long mSearchDateLast = 0;

}
