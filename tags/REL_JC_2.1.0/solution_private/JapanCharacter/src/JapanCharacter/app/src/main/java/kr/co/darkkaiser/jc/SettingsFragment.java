package kr.co.darkkaiser.jc;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.github.machinarius.preferencefragment.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName("jc_setup");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_settings_activity);

        final CheckBoxPreference chkHiragana = (CheckBoxPreference)findPreference("chk_hiragana");
        final CheckBoxPreference chkGatakana = (CheckBoxPreference)findPreference("chk_gatakana");
        final CheckBoxPreference chkYoum = (CheckBoxPreference)findPreference("chk_youm");

        chkHiragana.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean) newValue || chkGatakana.isChecked()) {
                    chkYoum.setEnabled(true);
                } else {
                    chkYoum.setEnabled(false);
                }

                return true;
            }
        });
        chkGatakana.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (chkHiragana.isChecked() || (boolean) newValue) {
                    chkYoum.setEnabled(true);
                } else {
                    chkYoum.setEnabled(false);
                }

                return true;
            }
        });

        if (getPreferenceScreen().getSharedPreferences().getBoolean("chk_hiragana", true) || getPreferenceScreen().getSharedPreferences().getBoolean("chk_gatakana", true)) {
            chkYoum.setEnabled(true);
        } else {
            chkYoum.setEnabled(false);
        }

        // 어플리케이션의 버전 정보를 구하여 화면에 출력한다.
        String versionName = getString(R.string.unknown_program_version);
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        PreferenceScreen prefProgramInfo = (PreferenceScreen) findPreference("jc_program_info");
        prefProgramInfo.setSummary(getString(R.string.app_name) + " " + getString(R.string.program_version) + " " + versionName);
    }

}
