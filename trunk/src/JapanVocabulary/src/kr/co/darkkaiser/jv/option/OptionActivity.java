package kr.co.darkkaiser.jv.option;

import kr.co.darkkaiser.jv.JvDefines;
import kr.co.darkkaiser.jv.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class OptionActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 타이틀을 설정한다.
		setTitle(String.format("%s - 환경설정", getResources().getString(R.string.app_name)));

		getPreferenceManager().setSharedPreferencesName(JvDefines.JV_SHARED_PREFERENCE_NAME);
		addPreferencesFromResource(R.layout.jv_optionlist);

		String versionName = "Unknown";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		SharedPreferences preferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);

		StringBuilder sb = new StringBuilder();
		sb.append("App. Version ").append(versionName)
		  .append("\nVocubulary DB Version ").append(preferences.getString(JvDefines.JV_SPN_DB_VERSION, "Unknown"));

		PreferenceScreen prefJvDbVersion = (PreferenceScreen)findPreference("jv_program_info");
		prefJvDbVersion.setSummary(sb.toString());
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
