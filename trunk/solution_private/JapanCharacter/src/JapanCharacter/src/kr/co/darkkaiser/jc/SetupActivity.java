package kr.co.darkkaiser.jc;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SetupActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName("jc_setup");
		addPreferencesFromResource(R.layout.jc_setup);

		CheckBoxPreference chkYoum = (CheckBoxPreference)findPreference("chk_youm");
		if (getPreferenceScreen().getSharedPreferences().getBoolean("chk_hiragana", true) == true || getPreferenceScreen().getSharedPreferences().getBoolean("chk_gatakana", true) == true) {
			chkYoum.setEnabled(true);
		} else {
			chkYoum.setEnabled(false);
		}

		// 어플리케이션의 버전 정보를 구하여 화면에 출력한다.
		String versionName = getString(R.string.unknown_program_version);
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.app_name)).append(" ").append(getString(R.string.program_version)).append(" ").append(versionName);

		PreferenceScreen prefProgramInfo = (PreferenceScreen)findPreference("jc_program_info");
		prefProgramInfo.setSummary(sb.toString());
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		CheckBoxPreference chkYoum = (CheckBoxPreference)findPreference("chk_youm");
		CheckBoxPreference chkHiragana = (CheckBoxPreference)findPreference("chk_hiragana");
		CheckBoxPreference chkGatakana = (CheckBoxPreference)findPreference("chk_gatakana");
		
		if (chkHiragana.isChecked() == true || chkGatakana.isChecked() == true) {
			chkYoum.setEnabled(true);
		} else {
			chkYoum.setEnabled(false);
		}
	}

}
