package kr.co.darkkaiser.jv.option;

import kr.co.darkkaiser.jv.JvDefines;
import kr.co.darkkaiser.jv.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

// @@@@@
public class OptionActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(JvDefines.JV_SHARED_PREFERENCE_NAME);
		addPreferencesFromResource(R.layout.jv_optionlist);

		SharedPreferences preferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		PreferenceScreen prefJvDbVersion = (PreferenceScreen)findPreference(JvDefines.JV_SPN_DB_VERSION);
		prefJvDbVersion.setSummary(preferences.getString(JvDefines.JV_SPN_DB_VERSION, "버전 정보를 확인할 수 없습니다."));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

}
