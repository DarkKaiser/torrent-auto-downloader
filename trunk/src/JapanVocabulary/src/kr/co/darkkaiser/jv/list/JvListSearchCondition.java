package kr.co.darkkaiser.jv.list;

import android.content.SharedPreferences;

public class JvListSearchCondition {
	
	private SharedPreferences mPreferences = null;
	
	public JvListSearchCondition(SharedPreferences preferences) {
		mPreferences = preferences;
	}
	
	// @@@@@ @@@@@ JLPT(�ܾ� ��� ����), ǰ��, �ϱ�Ϸ�/�̿Ϸ�/��ü, �ϱ���/����/��ü, ����� �˻�??
	private String mPartsOfSpeech = null;
	private String mSearchWord = null;
	private long mSearchDateFirst = 0;
	private long mSearchDateLast = 0;

}
